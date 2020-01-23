package uk.ac.brighton.jh1152.gallioncommanderv1;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Boat {


    private HashMap<String, BoatAction> actions;
    private HashMap<String, String> possibleInstructions;
    private String docRef;
    private FirebaseFirestore database;
    private CollectionReference actionsCollection;
    FirebaseFirestore db; // probably not here
    private Random random;

    public Boat(HashMap<String,BoatAction> actions , String documentReference){
        docRef = documentReference;
        this.actions = actions;
        db = FirebaseFirestore.getInstance();
        possibleInstructions = new HashMap<>();
        random = new Random();
        setPossibleInstructions();

    }


    public boolean setActionValue(String key, int value){
        BoatAction changingAction = actions.get(key);

        if(changingAction != null){
            changingAction.actionCurrent = value;
            setDocumentValue(changingAction);

            return changingAction.isActionComplete();
        }
        return false;
    }



    void setDocumentValue(BoatAction action){
        db.collection("boats/" + docRef +"/activities")
                .document(action.documentReference).update(action.getDocumentValues())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       // setPossibleInstructions();
                    }
                });
    }


    private void setPossibleInstructions(){
        CollectionReference actionscollection = db.collection("boats/"+ docRef +"/activities");
        // concider moving this to snapshot added method
        actionscollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.get("current") == document.get("target")){
                            possibleInstructions.remove(document.getId());
                        }
                        else{
                            int stateNumber = document.get("target", Integer.class);
                            List<String> states = (List<String>) document.get("states");
                            String stateName = states.get(stateNumber);
                            String instructionText = stateName +" the "+ document.get("name")  ;
                            possibleInstructions.put(document.getId(), instructionText);
                        }
                    }
                }
            }
        });
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

    // seperate instructons and actions this is shuit
   // have a look https://firebase.google.com/docs/firestore/manage-data/add-data
    public HashMap.Entry<String, String> getNewInstruction(){

        int mapSize = possibleInstructions.entrySet().size();
        Log.d("<<<<<<<<<<<<","size:     " + mapSize);

        int iterator = 0;
        HashMap.Entry<String, String> chosenInstruction = new AbstractMap.SimpleEntry<>("", "");
        if(mapSize  > 0) {
            int position = random.nextInt(mapSize);

            Log.d("<<<<<<<<<<<<","position:     " + position);

            for (Map.Entry<String, String> instruction : possibleInstructions.entrySet()) {
                if (iterator == position){
                    chosenInstruction = instruction;
                }
                iterator++;
            }
        }
        return chosenInstruction;
    }
}
