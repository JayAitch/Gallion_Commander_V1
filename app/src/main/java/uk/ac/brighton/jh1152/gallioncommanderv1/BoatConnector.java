package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.os.CountDownTimer;
import android.os.Debug;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class BoatConnector {

    FirebaseFirestore db;
    CollectionReference activitiesCollection;
    DocumentReference boatDocument;
    public Boat currentBoat;
    HashMap<String, BoatAction> boatActions;

    private static Long INSTRUCTION_TIME = (long) 10000;
    private InstructionTicker instructionTicker;
    private InstructionManager instructionManager;

    // temporary
    MainGameActivity activity;
    int playerPosition;
    int playerAmnt;
    Random random;
    String documentReference;

    private final String BOAT_COLLECTION = "boats/";
    private final String  ACTIVITIES_COLLECTION = "/activities";

    public BoatConnector (MainGameActivity activity, int playerPosition){
        this.activity = activity; //temprory
        this.playerPosition = playerPosition; //temp
        db = FirebaseFirestore.getInstance();
        instructionManager = new InstructionManager();
        boatActions = new HashMap<>();
        random = new Random();

    }



    public void formBoatFromDocument(final String documentID){
        boatDocument = db.document(BOAT_COLLECTION + documentID);
        activitiesCollection = boatDocument.collection(ACTIVITIES_COLLECTION);


        boatDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    playerAmnt = task.getResult().get("players", Integer.class); //temp
                    int lives = task.getResult().get("lives", Integer.class); //temp

                    activitiesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){

                                int activitiesSize = task.getResult().size();
                                int activityPosition = 0;

                                for (QueryDocumentSnapshot document: task.getResult()){

                                    BoatAction tempAction = createActionFromDocumentData(document);
                                    boatActions.put(document.getId(), tempAction);
                                    manageInstructionList(tempAction);

                                    if(isShowingActivityToPlayer(activityPosition, activitiesSize)){
                                        activity.addActionButton(tempAction);// temporary
                                    }
                                    activityPosition++;
                                    loadAfterBoatInit(activitiesSize, activityPosition);

                                }
                            }
                        }
                    });

                    currentBoat = new Boat(boatActions, documentID, lives);

                }
            }
        });



        // setup something to remove/ add these on pause/play
        activitiesCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(DocumentChange change: queryDocumentSnapshots.getDocumentChanges()){
                    switch (change.getType()){
                        case MODIFIED:
                            boatActivityChangeCallback(change.getDocument());
                    }
                }
            }
        });

        boatDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

            }
        });

    }

    public int getActionsRemainingAmount(){
        return instructionManager.getInstructionsSize();
    }

    private void loadAfterBoatInit(int size, int current){
        if(current == size){
            instructionManager.setRandomInstruction();
            initInstructionTicker();
            activity.updateUI();
        }
    }


    // probably not here
    private void initInstructionTicker(){
        ProgressBar progressBar = activity.findViewById(R.id.instruction_progress_bar);
        TextView instructionText = activity.findViewById(R.id.instruction_text);
        instructionTicker = new InstructionTicker(this, progressBar, instructionText, INSTRUCTION_TIME);
        instructionTicker.displayInstructionText();
    }



    private boolean isShowingActivityToPlayer(int activityPosition, int activitiesSize){
        boolean isShowingToPlayer = false;
        float activitiesPerUser = activitiesSize / (float)playerAmnt;
        int activitiesMin = (int)Math.floor(activitiesPerUser * playerPosition);
        int activitesMax = (int)Math.ceil(activitiesMin + activitiesPerUser);


        if(activityPosition <= activitesMax && activityPosition >= activitiesMin){
            isShowingToPlayer = true;
        }
        return isShowingToPlayer;
    }


    private BoatAction createActionFromDocumentData(QueryDocumentSnapshot document){
        String name = document.get("name", String.class);
        int target = document.get("target", Integer.class);
        int current = document.get("current", Integer.class);
        List<String> states = (List<String>) document.get("states");
        String[] statesArray = states.toArray(new String[states.size()]);
        BoatAction boatAction = new BoatAction(name, target, current, document.getId(), statesArray);
        return boatAction;
    }


    public void boatActivityChangeCallback(QueryDocumentSnapshot documentSnapshot){
        currentBoat.setLocalValue(documentSnapshot.getId(), documentSnapshot.get("current", Integer.class));
        manageInstructionList(currentBoat.actions.get(documentSnapshot.getId()));
        activity.updateUI();
       if(areAllActivitiesComplete()){
            stopGame();
            testNewLevel();
        }
    }


    //change this jank into states, might want a class to update the UI centrally
    // therer are 2 forms of upodates depending on the document change
    private void currentInstructionComplete(){
        instructionTicker.stopTimer();
        instructionTicker.displayInstructionText();
        instructionTicker.startTimer();
    }


    private void removeALife() {
            Map<String, Object> boatData = currentBoat.getData();
            boatData.put("lives", FieldValue.increment(-1));
            activity.updateUI();
            currentBoat.removeALife();
            boatDocument.update(boatData);
    }




    public void instructionTimeOut(){
        removeALife();
        if (currentBoat.isBoatAlive()) {
            instructionManager.setRandomInstruction();
            instructionTicker.displayInstructionText();
            instructionTicker.startTimer();
            if(areAllActivitiesComplete()){
                stopGame();
                //testNewLevel();
            }
        }
        else{
            instructionTicker.displayInstructionText();
        }
    }


    private void stopGame(){
        instructionTicker.stopTimer();
    }

    private void manageInstructionList(BoatAction action) {

        instructionManager.manageInstructionList(action);
        if(instructionManager.isCurrentInstruction(action.documentReference)){
            instructionManager.setRandomInstruction();
            currentInstructionComplete();
        }

    }


    public String getCurrentInstructionString(){
        Log.d("getting instruction", "" + currentBoat.livesRemaining);
        if(!currentBoat.isBoatAlive()) {
            return "Game Over!";
        }
        else if(areAllActivitiesComplete()){
            return "Complete!";
        }
        else if(!instructionManager.hasAnInstruction()){
            return "";
        }
        else{
            return instructionManager.getCurrentInstructionString();
        }

    }

    private boolean areAllActivitiesComplete(){
        boolean isComplete = true;
        for(Map.Entry<String, BoatAction> actionEntry: boatActions.entrySet()){
            BoatAction action = actionEntry.getValue();
            if(!action.isActionComplete()){
                isComplete = false;
            }
        }
        return isComplete;
    }



    private void testNewLevel(){
//        activity.removeAllActionButtons();
//        String[] tempStates4 = {"somehthing","somthing else"};
//        BoatAction newBoatAction = new BoatAction("Level2 thing", 0, 1, "3", Arrays.copyOf(tempStates4, tempStates4.length));
//        activitiesCollection.add(newBoatAction.getDocumentValues());
//        formBoatFromDocument(boatDocument.getId());

    }





}
