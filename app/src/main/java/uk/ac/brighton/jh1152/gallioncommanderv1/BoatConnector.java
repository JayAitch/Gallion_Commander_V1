package uk.ac.brighton.jh1152.gallioncommanderv1;

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


public class BoatConnector {

    FirebaseFirestore db;
    CollectionReference activitiesCollection;
    DocumentReference boatDocument;
    public Boat currentBoat;
    HashMap<String, String> boatInstructions;
    HashMap<String, BoatAction> boatActions;

    // temporary
    MainGameActivity activity;
    int playerPosition;
    int playerAmnt;

    private final String BOAT_COLLECTION = "boats/";
    private final String  ACTIVITIES_COLLECTION = "/activities";

    public BoatConnector (MainGameActivity activity, int playerPosition){
        this.activity = activity; //temprory
        this.playerPosition = playerPosition; //temp



        db = FirebaseFirestore.getInstance();
        boatInstructions = new HashMap<>();
        boatActions = new HashMap<>();
    }



    public void formBoatFromDocument(final String documentID){
        boatDocument = db.document(BOAT_COLLECTION + documentID);
        activitiesCollection = boatDocument.collection(ACTIVITIES_COLLECTION);


        boatDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    playerAmnt = task.getResult().get("players", Integer.class); //temp
                    activitiesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){

                                int activitiesSize = task.getResult().size();
                                int activityPosition = 0;

                                for (QueryDocumentSnapshot document: task.getResult()){

                                    BoatAction tempAction = createActionFromDocumentData(document);
                                    boatActions.put(document.getId(), tempAction);


                                    if(isShowingActivityToPlayer(activityPosition, activitiesSize)){
                                        activity.addActionButton(tempAction);// temporary
                                    }

                                    activityPosition++;
                                }
                            }
                        }
                    });

                    currentBoat = new Boat(boatActions, documentID);
                    activity.updateUI();
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



    private boolean isShowingActivityToPlayer(int activityPosition, int activitiesSize){
        boolean isShowingToPlayer = false;
        float activitiesPerUser = activitiesSize / (float)playerAmnt;
        int activitiesMin = (int)Math.floor(activitiesPerUser * playerPosition);
        int activitesMax = (int)Math.ceil(activitiesMin + activitiesPerUser);


        if(activityPosition <= activitesMax && activityPosition >= activitiesMin){
            isShowingToPlayer = true;
        }
        String message = " position:" + activityPosition +
                " players: " + playerAmnt +
                " Size: " + activitiesSize +
                " max: " + activitesMax +
                " min: " + activitiesMin;
        Log.d("isShowingToPlayer " +isShowingToPlayer ,message);
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



    public void boatDocumentChangeCallback(){

    }


    public void boatActivityChangeCallback(QueryDocumentSnapshot documentSnapshot){
        currentBoat.setActionValue(documentSnapshot.getId(), documentSnapshot.get("current", Integer.class));
        activity.updateUI();

        Log.d("<<<<<<<<<<<<<<","documnet thinks: " + documentSnapshot.get("current", Integer.class) + " this class thinks::" + boatActions.get(documentSnapshot.getId()).actionCurrent + " boat thinks::" + currentBoat.actions.get(documentSnapshot.getId()).actionCurrent);
    }



}
