package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

public class Boat {

    private BoatAction actions[];
    private HashMap<String, BoatAction> actions2;
    private String docRef;
    private FirebaseFirestore database;
    private CollectionReference actionsCollection;
    FirebaseFirestore db; // probably not here

    public Boat(int actionsAmount, String documentReference){
        docRef = documentReference;
        createActions();
    }

    public Boat(HashMap<String,BoatAction> actions , String documentReference){
        docRef = documentReference;
        actions2 = actions;
        db = FirebaseFirestore.getInstance();
    }


    private void createActions(){
        String[] states = {"lower","raise"};
        actions = new BoatAction[2];
        actions[0] = new BoatAction("the jolly rodger!", 1, 1, "0", Arrays.copyOf(states, 2));

        states[0] = "unsheith";
        states[1] = "shieth";

        actions[1] = new BoatAction("cutless lads!", 1, 0, "1", Arrays.copyOf(states, 2));
    }

    private void createAction(){

    }

    public boolean setActionValue(int pos, int value){
        BoatAction changingAction = actions[pos];
        changingAction.actionCurrent = value;
        return changingAction.isActionComplete();
    }



    public boolean setActionValue(String key, int value){
        BoatAction changingAction = actions2.get(key);


        changingAction.actionCurrent = value;
        setDocumentValue(changingAction);


        return changingAction.isActionComplete();
    }

    void setDocumentValue(BoatAction action){
        db.collection("boats/" + docRef +"/activities")
                .document(action.documentReference).update(action.getDocumentValues())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    public BoatAction[] getActions(){
        return actions;
    }
}
