package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.os.CountDownTimer;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class BoatConnector {

    FirebaseFirestore db;
    CollectionReference activitiesCollection;
    DocumentReference boatDocument;
    public Boat currentBoat;
    private HashMap<String, String> boatInstructions;
    HashMap<String, BoatAction> boatActions;
    private HashMap.Entry<String, String> currentInstruction;


    // temporary
    MainGameActivity activity;
    int playerPosition;
    int playerAmnt;
    Random random;

    private final String BOAT_COLLECTION = "boats/";
    private final String  ACTIVITIES_COLLECTION = "/activities";

    public BoatConnector (MainGameActivity activity, int playerPosition){
        this.activity = activity; //temprory
        this.playerPosition = playerPosition; //temp



        db = FirebaseFirestore.getInstance();
        boatInstructions = new HashMap<>();
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

    }

    public int getActionsRemainingAmount(){
        return boatInstructions.entrySet().size();
    }

    private void loadAfterBoatInit(int size, int current){
        if(current == size){

            setRandomInstruction();
            activity.updateUI();
        }
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
        Log.d("triggering callback---", "update");
    }





    private void manageInstructionList(BoatAction action) {

//        if(!areAllActivitiesComplete()) {

                if (action.isActionComplete()) {
                    removeFromInstructions(action.documentReference);
                    if(areAllActivitiesComplete())
                    {
                        activity.displayCompleteText();
                        currentInstruction = null;
                    }
                    else
                    {
                        if (currentInstruction == null ||currentInstruction.getKey() == action.documentReference) {
                            setRandomInstruction();
                        }
                    }

                } else {
                    addToInstructions(action);
                    if (currentInstruction == null ||currentInstruction.getKey() == action.documentReference){
                        setRandomInstruction();
                    }

                }




//        } else {
//            currentInstruction = null;
//            removeFromInstructions(action.documentReference);
//            activity.displayCompleteText();
//        }
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


    private void removeFromInstructions(String actionRef) {

        if(boatInstructions.containsKey(actionRef)){
            boatInstructions.remove(actionRef);
        }
    }


    private void addToInstructions(BoatAction action){
        boatInstructions.put(action.documentReference, action.getInstructionText());
    }



    private void setRandomInstruction(){
        int instructionSize = boatInstructions.entrySet().size();
        if(instructionSize > 0){
            int instructionsIncrementor = 0;
            int randomPosition = random.nextInt(instructionSize);
            for(Map.Entry<String, String> instruction: boatInstructions.entrySet()){

                if(instructionsIncrementor == randomPosition){
                    currentInstruction = instruction;
                }
                instructionsIncrementor++;
            }


            createInstructionCountdown();
        }
    }


    public void instructionTimeOut(){

    }


    // move this to another class
    CountDownTimer timer;

    private void createInstructionCountdown(){

        if(timer == null) {
            timer = new CountDownTimer(10000, 100) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setRandomInstruction();
                    activity.updateUI();
                }
            };
        }
        timer.start();
    }

    public String getCurrentInstructionString(){
        if(currentInstruction == null) return "";
        return currentInstruction.getValue();
    }
}
