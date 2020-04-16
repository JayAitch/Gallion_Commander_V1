package uk.ac.brighton.jh1152.gallioncommanderv1;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;


public class Boat {


    public HashMap<String, BoatAction> actions;
    private String docRef; //temp
    public int livesRemaining;
    private FirebaseFirestore db;

    public Boat(HashMap<String,BoatAction> actions , String documentReference, int livesRemaining){
        this.livesRemaining = livesRemaining;
        docRef = documentReference;
        this.actions = actions;
        db = FirebaseFirestore.getInstance();
    }

    // work out which action has changed and trigger display update
    public void setLocalValue(String key, int value){
        BoatAction changingAction = actions.get(key);
        if(changingAction != null){
            changingAction.actionCurrent = value;
        }
    }

    // commit changes from UI actions to the database
    public boolean setActionValue(String key, int value){
        // which action has changed
        BoatAction changingAction = actions.get(key);

        if(changingAction != null){
            // set the action value
            changingAction.actionCurrent = value;
            // commit changes
            setDocumentValue(changingAction);
            return changingAction.isActionComplete();
        }
        return false;
    }

    public boolean isBoatAlive(){
        return (livesRemaining > 0);
    }

    // commit parameterised action to the database
    void setDocumentValue(BoatAction action){
        db.collection(DocumentLocations.BOAT_COLLECTION + "/" + docRef +"/" + DocumentLocations.ACTION_COLLECTION)
                .document(action.documentReference).update(action.getDocumentValues())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }

    // values that are represented in documents.
    public Map<String, Object> getData(){
        HashMap<String, Object> boatData  = new HashMap<>();
        boatData.put("lives", livesRemaining);
        return boatData;
    }
}
