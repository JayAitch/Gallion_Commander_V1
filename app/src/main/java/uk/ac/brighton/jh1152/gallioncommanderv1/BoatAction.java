package uk.ac.brighton.jh1152.gallioncommanderv1;

import java.util.HashMap;
import java.util.Map;

public class BoatAction {
    int actionCurrent;
    int actionTarget;
    String docRef;
    String actionName;
    Map<String, Object> actionDBValues;

    public BoatAction(String name, int target, int current, String ref){
        actionName = name;
        actionTarget = target;
        actionCurrent = current;
        docRef = ref;
    }

    public Map<String, Object> getDocumentValues(){
        actionDBValues= new HashMap<>();
        actionDBValues.put("name", actionName);
        actionDBValues.put("target", actionTarget);
        actionDBValues.put("current", actionCurrent);
        return actionDBValues;
    }

    public boolean isActionComplete(){

        if(actionCurrent == actionTarget){
            return true;
        }
        return false;
    }
}
