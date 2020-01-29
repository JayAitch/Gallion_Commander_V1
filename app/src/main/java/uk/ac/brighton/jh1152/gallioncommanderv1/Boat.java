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

    private FirebaseFirestore db; // probably not here

    public Boat(HashMap<String,BoatAction> actions , String documentReference, int livesRemaining){
        this.livesRemaining = livesRemaining;
        docRef = documentReference;
        this.actions = actions;
        db = FirebaseFirestore.getInstance();
    }

    public void setLocalValue(String key, int value){
        BoatAction changingAction = actions.get(key);
        if(changingAction != null){
            changingAction.actionCurrent = value;
        }
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

    public boolean isBoatAlive(){
        return (livesRemaining > 0);
    }

    public void removeALife(){
        livesRemaining--;
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


    public Map<String, Object> getData(){
        HashMap<String, Object> boatData  = new HashMap<>();
        boatData.put("lives", livesRemaining);
        return boatData;
    }
}
