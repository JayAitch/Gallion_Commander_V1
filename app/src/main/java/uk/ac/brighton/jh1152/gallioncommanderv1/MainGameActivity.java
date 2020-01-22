package uk.ac.brighton.jh1152.gallioncommanderv1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.widget.TextView;

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

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainGameActivity extends AppCompatActivity {
    Boat boat;
    String gameID;// = "EiDo3HKycS8ckYxdMNGw";
    int playerNumber;
    FirebaseFirestore db;
    HashMap<String, BoatAction> activities;
    HashMap<String, IBaseBoatActionUI> boatActionButtons;


    TextView instructionTextDisplay;
    HashMap.Entry<String, String> currentInstruction;
    //public static final String EXTRA_MESSAGE  = "uk.ac.brighton.jh1152.gallioncommanderv1.BOATID";
   // public static final String EXTRA_PLAYER_NUMBER = "uk.ac.brighton.jh1152.gallioncommanderv1.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        gameID = intent.getStringExtra(LobbyActivity.EXTRA_BOAT_ID);
         playerNumber = intent.getIntExtra(LobbyActivity.EXTRA_PLAYER_NUMBER, -1);
         Log.d("<<<<<<<<<<<<<<<<<<<", "player number"+ playerNumber);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.game_layout);
        instructionTextDisplay = findViewById(R.id.instruction_text);
        GetActions();
    }



    private void createActionButtons(HashMap<String,BoatAction> actions){
        int iterator = 0;
        boatActionButtons = new HashMap<>();

        for (Map.Entry<String, BoatAction> entry: actions.entrySet()) {
            Log.d("game activitive", "" + iterator);
            IBaseBoatActionUI newUIAction;
            if(entry.getValue().states.length > 2){
                // consider not adding states at all
                newUIAction = new BoatActionSlider(this, boat, iterator, entry.getValue());
            }else{
                newUIAction = new BoatActionToggle(this, boat, iterator, entry.getValue());
            }

            boatActionButtons.put(entry.getKey(),newUIAction);
        }
    }

    float playerSize;
    float actionRoleMax = 0;
    float actionRoleMin = 0;
    float stepsPerUser;
    float actionsSize;
    int documentCount;


    private void GetActions(){
        /// probably waant a single object with exlusive db access



        activities = new HashMap<>();

        DocumentReference boatDocument = db.document("boats/" + gameID);

        boatDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                     @Override
                                                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                         DocumentSnapshot document = task.getResult();
                                                         playerSize = document.get("players", Integer.class);

                CollectionReference actionscollection = db.collection("boats/"+gameID+"/activities");
                // concider moving this to snapshot added method
                actionscollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful()) {

                            //not like this
                            actionsSize = task.getResult().size();
                            stepsPerUser  =  (actionsSize/ (playerSize + 1));

                            Log.d("<<<<<<<<calc", "playersuze:  " + playerSize +"apparent ssize:  " + actionsSize);
                            actionRoleMin =  stepsPerUser * playerNumber;
                            actionRoleMax =  actionRoleMin + stepsPerUser;


                            Log.d("<<<<<<<<calc", "size: " + actionsSize + " pos: " + playerNumber +" players: " + playerSize + " spu: " + stepsPerUser + " Role:" + actionRoleMin +" - "+ actionRoleMax + "cond1: " + (documentCount >= actionRoleMin) + "cond2: " + (documentCount <= actionRoleMax));
                            int documentCount = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                if(documentCount >= actionRoleMin && documentCount <= actionRoleMax){
                                    String activityKey = (String) document.get("name");
                                    int target = (int) (long) document.get("target");
                                    int current = (int) (long) document.get("current");
                                    // String[] states = (ArrayList<>) document.get("states");
                                    String[] stateActions;


                                    List<String> statesMap = (List<String>) document.get("states");
                                    stateActions = new String[statesMap.size()];

                                    Log.d("<<<<<<<<calc", "size: " + actionsSize + " players: " + playerSize + " spu: " + stepsPerUser + " Role:" + actionRoleMin +" - "+ actionRoleMax + "cond1: " + (documentCount > actionRoleMin) + "cond2: " + (documentCount < actionRoleMax));


                                    int iterator = 0;


                                    for (String state : statesMap) {

                                        stateActions[iterator] = state;
                                        iterator++;
                                    }

                                    BoatAction tempAction = new BoatAction(activityKey, target, current, document.getId(), stateActions);
                                    activities.put(document.getId(), tempAction);



                                }
                                documentCount++;
                            }

                        } else {

                        }
                        boat = new Boat(activities, gameID);
                        createActionButtons(activities);
                        UpdateUI();
                    }
                });





        actionscollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for(DocumentChange change: queryDocumentSnapshots.getDocumentChanges()){
                        switch (change.getType()){
                        case MODIFIED:

                            boat.setActionValue(change.getDocument().getId(),(int)(long) change.getDocument().get("current"));

                            UpdateUI();
                    }
                }
            }
        });


                                                     }
        });

    }







    private void UpdateUI(){

        for (Map.Entry<String, IBaseBoatActionUI> entry: boatActionButtons.entrySet()) {
            IBaseBoatActionUI UIAction = entry.getValue();
            UIAction.setTextValue();
        }

        if (currentInstruction != null && !boat.isShipComplete()) {

             if (!boat.isInstructionPossible(currentInstruction.getKey())) {
                currentInstruction = boat.getNewInstruction();
                Log.d("<<<<<<<<<<<<<<<", "instruction is" + currentInstruction.getValue());
                displayCurrentInstruction();

            }

         } else {
            currentInstruction = boat.getNewInstruction();
            displayCurrentInstruction();
        }
        displayCurrentInstruction();
    }


    private void displayCurrentInstruction(){
        if(boat.isShipComplete()){
            instructionTextDisplay.setText("complete");
        }
        else{
            if(currentInstruction != null) {
                instructionTextDisplay.setText(currentInstruction.getValue());
            }



        }


    }





    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

}
