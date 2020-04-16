package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;

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
    private Activity activity;

    // temporary class to store action creation values
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
        this.activity = activity;
        random = new Random();
        buildPossibleActions();
    }


    // use json defined in external file to build list of possible actions
    private String getPossibleActionsFromFile(){
        String possibleActionsString = null;
        try {
            // use the binnary reader to interpret file as a string
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
            // build possible actions from external file
            JSONArray jsonArray = new JSONArray(getPossibleActionsFromFile());
            possibleActions = new PossibleAction[jsonArray.length()];

            // go through the json and create a list of all the possible actions
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                JSONArray JSONstates = jsonObject.getJSONArray("states");
                String[] states = new String[JSONstates.length()];

                // create an array of states possible actions
                for(int statesi = 0; statesi < JSONstates.length(); statesi++){
                    states[statesi] = JSONstates.getString(statesi);
                }

                // get the name of the action and the type of control it needs.
                String name = jsonObject.getString("name");
                String type = jsonObject.getString("type");
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
            position = roleAPosition();
            if(position != -1){
                chosenBoatActions.add(createFinishedAction(position));
                iOverallPosition++;
            }

        }

        for(int iNotFinishedAmnt = 0; iNotFinishedAmnt < notFinishedAmnt; iNotFinishedAmnt++){
            position = roleAPosition();
            if(position != -1) {

                chosenBoatActions.add(createUnfinishedAction(position));
                iOverallPosition++;
            }
        }

        return chosenBoatActions.toArray(new BoatAction[chosenBoatActions.size()]);
    }

    private int roleAPosition(){

        int maxSize = possibleActions.length;
        if(maxSize <= 0) return -1;
        return random.nextInt(maxSize);
    }

    private BoatAction createFinishedAction(int position){

        PossibleAction possibleAction = possibleActions[position];

        String actionRef = possibleAction.actionType.name() + possibleAction.name;
        BoatAction action = new BoatAction(possibleAction.name, possibleAction.actionType, 0, 0, actionRef, possibleAction.states);
        return action;
    }


    private BoatAction createUnfinishedAction(int position){
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


}
