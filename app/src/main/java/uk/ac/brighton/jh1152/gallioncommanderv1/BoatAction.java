package uk.ac.brighton.jh1152.gallioncommanderv1;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BoatAction {
    int actionCurrent;
    int actionTarget;
    String documentReference;
    String actionName;
    Map<String, Object> actionDBValues;
    String states[];
    BoatActionControlType controlType;


    public BoatAction(String name, BoatActionControlType type, int target, int current, String ref, String stateNames[]){
        actionName = name;
        actionTarget = target;
        actionCurrent = current;
        documentReference = ref;
        states = stateNames;
        controlType = type;

    }

    public Map<String, Object> getDocumentValues(){
        actionDBValues= new HashMap<>();
        actionDBValues.put("name", actionName);
        actionDBValues.put("type", controlType.name());
        actionDBValues.put("target", actionTarget);
        actionDBValues.put("current", actionCurrent);
        actionDBValues.put("states", Arrays.asList(states));
        return actionDBValues;
    }

    public String getInstructionText(){
        String instructionText;
        String stateText;
        stateText = states[actionTarget];
        instructionText = stateText + " the " + actionName;
        return instructionText;
    }

    public boolean isActionComplete(){
        return (actionCurrent == actionTarget);
    }
}
