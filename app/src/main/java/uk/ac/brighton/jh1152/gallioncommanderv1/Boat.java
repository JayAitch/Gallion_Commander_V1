package uk.ac.brighton.jh1152.gallioncommanderv1;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;


public class Boat {


    public HashMap<String, BoatAction> actions;
    private HashMap<String, String> possibleInstructions;
    private String docRef;
    private FirebaseFirestore database;
    private CollectionReference actionsCollection;
    FirebaseFirestore db; // probably not here

    public Boat(HashMap<String,BoatAction> actions , String documentReference){
        docRef = documentReference;
        this.actions = actions;
        db = FirebaseFirestore.getInstance();
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
                    }
                });
    }
}
