package uk.ac.brighton.jh1152.gallioncommanderv1;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Boat {

    private BoatAction actions[];
    private String docRef;
    private FirebaseFirestore database;
    private CollectionReference actionsCollection;


    public Boat(int actionsAmount, String documentReference){
        docRef = documentReference;
        createActions();
    }




    private void createActions(){
        actions = new BoatAction[2];
        actions[0] = new BoatAction("raise the jolly rodger!", 1, 1, "0");
        actions[1] = new BoatAction("walk the plank", 1, 0, "1");
    }


    public boolean setActionValue(int pos, int value){
        BoatAction changingAction = actions[pos];
        changingAction.actionCurrent = value;
        return changingAction.isActionComplete();
    }

    public BoatAction[] getActions(){
        return actions;
    }
}
