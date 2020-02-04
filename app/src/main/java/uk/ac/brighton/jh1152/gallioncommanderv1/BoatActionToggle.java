package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.Switch;


public class BoatActionToggle implements IBaseBoatActionUI {


    Boat lBoat;
    BoatAction action;
    boolean isToggled;
    ActionLayoutWithToggleButton actionButton;

    public BoatActionToggle(Activity activity, Boat boat, BoatAction boatAction) {
        lBoat = boat;
        action = boatAction;
        actionButton = (ActionLayoutWithToggleButton) new ActionLayoutWithToggleButton(activity, action.actionName, action.states, action.actionCurrent);

        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.activitiesGrid);
        layout.addView(actionButton);
        valueChangeCallback();
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetBoatAction();
            }
        });
    }

    @Override
    public void valueChangeCallback(){

        isToggled = (action.actionCurrent == 1);
       actionButton.setCurrentValue(isToggled ? 0: 1);
    }

    @Override
    public Boolean SetBoatAction() {
        isToggled = !isToggled;
        lBoat.setActionValue(action.documentReference, isToggled ? 1: 0);
        return isToggled;
    }
}
