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
        this.activity = activity;
        random = new Random();
        buildPossibleActions();
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
