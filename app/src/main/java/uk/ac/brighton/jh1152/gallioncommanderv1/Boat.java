package uk.ac.brighton.jh1152.gallioncommanderv1;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Boat {


    private HashMap<String, BoatAction> actions;
    private HashMap<String, String> possibleInstructions;
    private String docRef;
    private FirebaseFirestore database;
    private CollectionReference actionsCollection;
    FirebaseFirestore db; // probably not here


    public Boat(HashMap<String,BoatAction> actions , String documentReference){
        docRef = documentReference;
        this.actions = actions;
        db = FirebaseFirestore.getInstance();
        possibleInstructions = new HashMap<>();
        setPossibleInstructions();
    }


    public boolean setActionValue(String key, int value){
        BoatAction changingAction = actions.get(key);


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
                        setPossibleInstructions();
                    }
                });
    }


    // method is being called too many times
    private void setPossibleInstructions(){

        for (Map.Entry<String, BoatAction> entry: actions.entrySet()) {
                BoatAction boatAction = entry.getValue();
                if(boatAction.isActionComplete()){
                    possibleInstructions.remove(entry.getKey());
                }
                else{
                    String instructionText = boatAction.states[boatAction.actionTarget] +" the "+ boatAction.actionName  ;
                    Log.d("<<<<<<<<<<<<<", "instructionText:  "  + instructionText);
                    possibleInstructions.put(entry.getKey(), instructionText);
                }
        }
    }


    public boolean isInstructionPossible(String key){
        if(key == "") return false;
        if(possibleInstructions.entrySet().size() > 0){
            return possibleInstructions.containsKey(key);
        }
        else{
            return false;
        }
    }

    public boolean isShipComplete(){
        setPossibleInstructions();
        return possibleInstructions.entrySet().isEmpty();
    }


    public HashMap.Entry<String, String> getNewInstruction(){

        int mapSize = possibleInstructions.entrySet().size();

        int iterator = 0;
        HashMap.Entry<String, String> chosenInstruction = new AbstractMap.SimpleEntry<>("", "");
        if(mapSize > 0) {
            int position = new Random().nextInt(mapSize);
            for (Map.Entry<String, String> instruction : possibleInstructions.entrySet()) {
                if (iterator == position) ;
                chosenInstruction = instruction;
                iterator++;
            }
        }
        return chosenInstruction;
    }
}
