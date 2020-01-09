package uk.ac.brighton.jh1152.gallioncommanderv1;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.Arrays;

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

    public BoatAction[] getActions(){
        return actions;
    }
}
