package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class ActionCreator {
    private PossibleAction[]possibleActions;
    private Random random;
    private Activity activity; //temp


    private class PossibleAction{
        public String name;
        public String[] states;
        public BoatActionControlType actionType;

        public PossibleAction(String name, String[] states, BoatActionControlType actionType){
            this.name = name;
            this.states = states;
            this.actionType = actionType;
        }
    }


    public ActionCreator(Activity activity){
        this.activity = activity; //temp
        random = new Random();
        buildPossibleActions();
//        possibleActions = new PossibleAction[6];
//        String[] tempStates = {"release","capture"};
//        possibleActions[0] = new PossibleAction("Kraken", Arrays.copyOf(tempStates, tempStates.length), BoatActionControlType.TOGGLE);
//
//        String[] tempStates2 = {"unload","load"};
//        possibleActions[1] = new PossibleAction("Cannons",  Arrays.copyOf(tempStates2, tempStates2.length), BoatActionControlType.TOGGLE);
//
//       String[] tempStates3 = {"raise","lower"};
//        possibleActions[2] = new PossibleAction("Jolly Rodger",  Arrays.copyOf(tempStates3, tempStates3.length), BoatActionControlType.TOGGLE);
//
//        String[] tempStates4 = {"down","ready","up"};
//        possibleActions[3] = new PossibleAction("Rudder",  Arrays.copyOf(tempStates4, tempStates4.length), BoatActionControlType.SLIDER);
//
//        String[] tempStates5 = {"unfurl", "furl"};
//        possibleActions[4] =new PossibleAction("Sails",  Arrays.copyOf(tempStates5, tempStates5.length), actionTypes.TOGGLE);
//
//        String[] tempStates6 = {"stow","get out"};
//        possibleActions[5] =new PossibleAction("Rum", Arrays.copyOf(tempStates6, tempStates6.length), actionTypes.TOGGLE);
//
//        String[] tempStates7 = {"cage", "uncage"};
//        possibleActions[6] =new PossibleAction("Parrot", Arrays.copyOf(tempStates7, tempStates7.length), actionTypes.TOGGLE);
//
//        String[] tempStates8 = {"start", "stop"};
//        possibleActions[7] =new PossibleAction("ERRing",Arrays.copyOf(tempStates8, tempStates8.length), actionTypes.TOGGLE);

//        String[] tempStates9 = {"quarter mast", "half mast", "full-mast"};
//        possibleActions[4] =new PossibleAction("Sails",  Arrays.copyOf(tempStates9, tempStates9.length), BoatActionControlType.SLIDER);
//
//
//        String[] tempStates5 = {"North", "East", "South", "West"};
//        possibleActions[5] =new PossibleAction("Direction",  Arrays.copyOf(tempStates5, tempStates5.length), BoatActionControlType.SLIDER);
//
//

//        String[] tempStates10 = {"Avast", "Back to duties"};
//        possibleActions[9] =new PossibleAction("Me harties", Arrays.copyOf(tempStates10, tempStates10.length), actionTypes.TOGGLE);

//        String[] tempStates11 = {"Sing", "Silence"};
//        possibleActions[10] =new PossibleAction("Shanty", Arrays.copyOf(tempStates11, tempStates11.length), actionTypes.TOGGLE);
//
//        String[] tempStates12 = {"recover", "throw"};
//        possibleActions[11] =new PossibleAction("Gold", Arrays.copyOf(tempStates12, tempStates12.length), actionTypes.TOGGLE);
//
//        String[] tempStates13 = {"down", "up"};
//
//        possibleActions[12] =new PossibleAction("Anchors", Arrays.copyOf(tempStates13, tempStates13.length), actionTypes.TOGGLE);
//
//        String[] tempStates14 = {"Man", "Unman"};
//        possibleActions[13] =new PossibleAction("Helm", Arrays.copyOf(tempStates14, tempStates14.length), actionTypes.TOGGLE);
//
//        String[] tempStates15 = {"Start", "Stop"};
//        possibleActions[14] =new PossibleAction("Fishing", Arrays.copyOf(tempStates15, tempStates15.length), actionTypes.TOGGLE);

    }


// https://stackoverflow.com/questions/19945411/android-java-how-can-i-parse-a-local-json-file-from-assets-folder-into-a-listvi/19945484#19945484
// https://stackoverflow.com/questions/40565078/how-to-add-json-file-to-android-project
    private String getPossibleActionsFromFile(){
        String possibleActionsString = null;
        try {
            InputStream inputStream = activity.getAssets().open("possible-actions.json");
            int streamSize = inputStream.available();
            byte[] buffer = new byte[streamSize];
            inputStream.read(buffer);
            inputStream.close();
            possibleActionsString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  possibleActionsString;
    }

    private void buildPossibleActions(){
        try {
            JSONArray jsonArray = new JSONArray(getPossibleActionsFromFile());
            possibleActions = new PossibleAction[jsonArray.length()];
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                JSONArray JSONstates = object.getJSONArray("states");
                String[] states = new String[JSONstates.length()];
                for(int statesi = 0; statesi < JSONstates.length(); statesi++){
                    states[statesi] = JSONstates.getString(statesi);
                }
                String name = object.getString("name");
                String type = object.getString("type");
                BoatActionControlType controlType = BoatActionControlType.valueOf(type);
                possibleActions[i] = new PossibleAction(name,states, controlType);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public BoatAction[] getRandomActions(int finishedAmnt, int notFinishedAmnt){
        ArrayList<BoatAction> chosenBoatActions = new ArrayList<>();
        int iOverallPosition = 0;
        int position = 0;


        for(int ifinishedAmnt = 0; ifinishedAmnt < finishedAmnt; ifinishedAmnt++){
            Log.d("made some actions", "position not finished" + position);
            position = roleAPosition();
            if(position != -1){
                chosenBoatActions.add(createRandomFinishedAction(position));
                iOverallPosition++;
            }

        }

        for(int iNotFinishedAmnt = 0; iNotFinishedAmnt < notFinishedAmnt; iNotFinishedAmnt++){
            Log.d("made some actions", "position not finished" + position);
            position = roleAPosition();
            if(position != -1) {

                chosenBoatActions.add(createRandomUnfinishedAction(position));
                iOverallPosition++;
            }
        }

        Log.d("made some actions", "chosenBoatActions" + chosenBoatActions.size());
        return chosenBoatActions.toArray(new BoatAction[chosenBoatActions.size()]);
    }

    private int roleAPosition(){

        int maxSize = possibleActions.length;
        if(maxSize <= 0) return -1;
        return random.nextInt(maxSize);
    }

    private BoatAction createRandomFinishedAction(int position){

        PossibleAction possibleAction = possibleActions[position];

        String actionRef = possibleAction.actionType.name() + possibleAction.name;
        BoatAction action = new BoatAction(possibleAction.name, possibleAction.actionType, 0, 0, actionRef, possibleAction.states);
        return action;
    }


    private BoatAction createRandomUnfinishedAction(int position){
        BoatAction boatAction;
        PossibleAction possibleAction = possibleActions[position];
        String actionRef = possibleAction.actionType.name() + possibleAction.name;
        int roll = random.nextInt(10);

        if(roll > 5){
            boatAction= new BoatAction(possibleAction.name, possibleAction.actionType,0, 1, actionRef, possibleAction.states);
        }
        else{
            boatAction= new BoatAction(possibleAction.name, possibleAction.actionType, 1, 0, actionRef, possibleAction.states);
        }

        return boatAction;
    }

//    public BoatAction createAction(String actionName,BoatActionControlType, int target, int current, String onActionState, String offActionState){
//        String[] tempStates = {onActionState,offActionState};
//        return new BoatAction(actionName, possibleAction.actionType, target, current, "0", Arrays.copyOf(tempStates, tempStates.length));
//    }
//
//    public BoatAction createAction(String actionName, int target, int current, String[] states){
//        return new BoatAction(actionName, target, current, "0", Arrays.copyOf(states, states.length));
//    }



}
