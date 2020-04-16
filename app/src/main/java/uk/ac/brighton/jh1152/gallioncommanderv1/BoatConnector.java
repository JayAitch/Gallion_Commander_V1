package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BoatConnector {

    FirebaseFirestore db;
    CollectionReference actionCollection;
    DocumentReference boatDocument;
    public Boat currentBoat;
    HashMap<String, BoatAction> boatActions;
    InstructionTicker instructionTicker;
    InstructionManager instructionManager;

    MainGameActivity activity;
    int playerPosition;
    int playerAmnt;
    int level = 0;
    final String BOAT_COLLECTION = DocumentLocations.BOAT_COLLECTION ;
    final String ACTION_COLLECTION = DocumentLocations.ACTION_COLLECTION ;
    enum boatStates {TRAVELLING,COMPLETE,DEAD};
    ActionCreator actionCreator;
    ListenerRegistration collectionListener;
    ListenerRegistration boatListener;



    private Dialog levelCompleteDialogue;
    private CountDownTimer dialogueTimer;



    public BoatConnector (MainGameActivity activity, int playerPosition){
        this.activity = activity;
        this.playerPosition = playerPosition;
        db = FirebaseFirestore.getInstance();
        instructionManager = new InstructionManager();

        actionCreator = new ActionCreator(activity);
        ProgressBar progressBar = activity.findViewById(R.id.instruction_progress_bar);
        TextView instructionText = activity.findViewById(R.id.instruction_text);
        createLevelCompleteDialogue();
        instructionTicker = new InstructionTicker(this, progressBar, instructionText, GameSettings.BASE_INSTRUCTION_TIMER);
    }


    // query boat document and construct game objects
    public void formBoatFromDocument(final String documentID){
        boatDocument = db.document(BOAT_COLLECTION +"/"+ documentID);
        actionCollection = boatDocument.collection(ACTION_COLLECTION);
        boatActions = new HashMap<>();

        boatDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    playerAmnt = task.getResult().get("players", Integer.class); //temp
                    int lives = task.getResult().get("lives", Integer.class); //temp

                    actionCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){

                                int actionsSize = task.getResult().size();
                                int actionPosition = 0;

                                for (QueryDocumentSnapshot document: task.getResult()){

                                    // build new actions on the interface based on the document data=
                                    BoatAction tempAction = createActionFromDocumentData(document);
                                    boatActions.put(document.getId(), tempAction);

                                    // either remove or add instruction based on its finished state
                                    manageInstructionList(tempAction);

                                    // only show some of the actions to each player
                                    if(shouldShowActionToPlayer(actionPosition, actionsSize)){
                                        activity.addActionButton(tempAction);// temporary
                                    }
                                    actionPosition++;
                                    // only trigger the init after this has finished building objects
                                    loadAfterBoatInit(actionsSize, actionPosition);

                                }
                            }
                        }
                    });

                    // set a local value for the boat
                    currentBoat = new Boat(boatActions, documentID, lives);

                }
            }
        });

    }



    private void createListeners(){
        // create a listener for the action collection
        collectionListener = actionCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(DocumentChange change: queryDocumentSnapshots.getDocumentChanges()){
                    // update all clients of any changes  to the actions
                    switch (change.getType()){
                        case MODIFIED:
                            boatActionChangeCallback(change.getDocument());
                    }
                }
            }
        });

        // listen to life changes on the boat document
        boatListener = boatDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                int lives = documentSnapshot.get("lives", int.class);
                currentBoat.livesRemaining = lives;
                activity.updateUI();
            }
        });
    }


    // clean up any listeners still running in the background
    public void destroyListeners(){
        boatListener.remove();
        collectionListener.remove();
        instructionTicker.stopTimer();
    }

    // how many actions are left uncomplete
    public int getActionsRemainingAmount(){
        return instructionManager.getInstructionsSize();
    }


    // only trigger the init after all documents have been read
    private void loadAfterBoatInit(int size, int current){
        if(current == size){
            // start listening to documents
            createListeners();
            // get a random instruction for this player
            instructionManager.setRandomInstruction();
            // start updating
            initInstructionTicker();
            activity.updateUI();
        }
    }


    // display the chosen instruction and start the updater
    private void initInstructionTicker(){
        instructionTicker.displayInstructionText();
        instructionTicker.startTimer();
    }


    // only show some actions to the player
    private boolean shouldShowActionToPlayer(int actionPosition, int actionSize){
        boolean isShowingToPlayer = false;
        float actionsPerUSer = actionSize / (float)playerAmnt;
        int activitiesMin = (int)Math.floor(actionsPerUSer * playerPosition);
        int actionMax = (int)Math.floor(activitiesMin + actionsPerUSer);

        // make sure every action is shown to at least one player
        if(actionPosition <= actionMax && actionPosition >= activitiesMin){
            isShowingToPlayer = true;
        }
        return isShowingToPlayer;
    }

    // build local value for action document
    private BoatAction createActionFromDocumentData(QueryDocumentSnapshot document){
        // get from the firebase document
        String name = document.get("name", String.class);
        int target = document.get("target", Integer.class);
        int current = document.get("current", Integer.class);
        String controlType = document.get("type", String.class);
        List<String> states = (List<String>) document.get("states");
        String[] statesArray = states.toArray(new String[states.size()]);
        // build a new action to represent this document data
        BoatAction boatAction = new BoatAction(name, BoatActionControlType.valueOf(controlType), target, current, document.getId(), statesArray);
        return boatAction;
    }


    // callback for any changes made to the parent document
    public void boatActionChangeCallback(QueryDocumentSnapshot documentSnapshot){

        currentBoat.setLocalValue(documentSnapshot.getId(), documentSnapshot.get("current", Integer.class));
        manageInstructionList(currentBoat.actions.get(documentSnapshot.getId()));
        activity.updateUI();

        // level is complete trigger a new level load
       if(getBoatState() == boatStates.COMPLETE){
            stopGame();
            formNewLevelActions();
        }
    }

    // restart the timer and get a new instruction when the action has been completed
    private void currentInstructionComplete(){
        instructionTicker.stopTimer();
        instructionTicker.displayInstructionText();
        instructionTicker.startTimer();
    }


    // an action has been failed, remove a life from the boat document
    private void removeALife() {
        Map<String, Object> boatData = currentBoat.getData();
        boatData.put("lives", FieldValue.increment(-1));
        boatDocument.update(boatData);
    }



    // check boat state and do an action based on the lives/instructions remaining
    public void instructionTimeOut(){
        removeALife();

        switch (getBoatState()) {
            case DEAD:
                stopGame();
                break;
            case TRAVELLING:
                // get a new instruction for the current player (can be the same one)
                instructionManager.setRandomInstruction();
                instructionTicker.displayInstructionText();
                instructionTicker.startTimer();
                break;
            case COMPLETE:
                stopGame();
                break;
        }
        instructionTicker.displayInstructionText();

    }

    // stop the client from performing timer based updates
    private void stopGame(){
        instructionManager.removeCurrentInstruction();
        instructionTicker.stopTimer();
    }


    // update instruction list based on whether the action that has changed is now completed
    private void manageInstructionList(BoatAction action) {
            instructionManager.manageInstructionList(action);
            // this action is complete get a new one
            if (instructionManager.isCurrentInstruction(action.documentReference) && action.isActionComplete()) {
                instructionManager.setRandomInstruction();
                currentInstructionComplete();
            }
    }


    // text to display in the instruction ticker
    // display the state if a new instruction shouldnt be fetched
    public String getCurrentInstructionString(){

        switch (getBoatState()){
            case DEAD:
                return "Game Over!";
            case COMPLETE:
                return "Complete!";
            case TRAVELLING:
                if(!instructionManager.hasAnInstruction()){
                    return "";
                }
                else{
                    return instructionManager.getCurrentInstructionString();
                }
            default:
                return "";
        }


    }

    // check all the actions on the boat
    private boolean areAllActionsComplete(){
        boolean isComplete = true;
        for(Map.Entry<String, BoatAction> actionEntry: boatActions.entrySet()){
            BoatAction action = actionEntry.getValue();
            if(!action.isActionComplete()){
                isComplete = false;
            }
        }
        return isComplete;
    }

    // work out what state the game is in, this is called asyncronously by document updates
   public boatStates getBoatState(){
        boatStates state;
       if(!currentBoat.isBoatAlive()) {
           state = boatStates.DEAD;
       }
       else if(areAllActionsComplete()){
           state = boatStates.COMPLETE;
       }
       else{
           state = boatStates.TRAVELLING;
       }
        return state;
   }





    // show level complete dialogue
    private void createLevelCompleteDialogue(){

        levelCompleteDialogue = new AlertDialog.Builder(activity)
                .setTitle("Level Complete").setMessage("Moving on the next level in \n 5").create();
    }

    // use the action creater to generate a brand new set of actions
    private void formNewLevelActions(){

        activity.removeAllActionButtons();
        destroyListeners();

        level++;
        if(playerPosition == 0) {

            BoatAction[] newActions = actionCreator.getRandomActions(GameSettings.BASE_FINISHED_ACTIVITIES + (level * GameSettings.ACTIVITIES_PER_LEVEL), GameSettings.BASE_UNFINISHED_ACTIVITIES + (level * GameSettings.ACTIVITIES_PER_LEVEL));

            for (int iActionsIterator = 0; iActionsIterator < newActions.length; iActionsIterator++) {

                BoatAction newBoatAction = newActions[iActionsIterator];
                actionCollection.document(newBoatAction.documentReference).set(newBoatAction.getDocumentValues());
            }
        }

        triggerLevelCompleteDialogue();

    }

    // trigger the level complete dialogue load new level when time reached
    private void triggerLevelCompleteDialogue(){


        levelCompleteDialogue.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {

                if(dialogueTimer == null){
                    // buiild countdown for 5 seconds to alert players to new level
                    dialogueTimer = new CountDownTimer(5000, 1000) {
                        final TextView messageText =  levelCompleteDialogue.findViewById(android.R.id.message);
                        @Override
                        public void onTick(long millisUntilFinished) {
                            // display the time left in the dialogue
                            int secondsLeft = (int) millisUntilFinished / 1000;
                            messageText.setText("Moving on the next level in " + secondsLeft);
                        }

                        @Override
                        public void onFinish() {
                            // trigger the new level to load
                            triggerNewLevelLoad();
                            // this is an issue when rotating the screen this will crash the application
                            levelCompleteDialogue.dismiss();
                        }
                    };
                    dialogueTimer.start();
                }
                else{
                    dialogueTimer.start();
                }
            }
        });
        levelCompleteDialogue.show();
    }

    // re-form objerct from boat docuent and actions collection
    private void triggerNewLevelLoad(){

        formBoatFromDocument(boatDocument.getId());
    }



}
