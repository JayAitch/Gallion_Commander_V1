package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;

import android.widget.LinearLayout;


public class BoatActionToggle implements IBaseBoatActionUI {


    Boat lBoat;
    BoatAction action;
    boolean isToggled;
    ControlToggleButton actionButton;

    public BoatActionToggle(Activity activity, Boat boat, BoatAction boatAction) {
        lBoat = boat;
        action = boatAction;
        actionButton = (ControlToggleButton) new ControlToggleButton(activity, action.actionName, action.states, action.actionCurrent);

        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.activitiesGrid);
        layout.addView(actionButton);
        valueChangeCallback();

        actionButton.setControlListener(new IControlListener() {
            @Override
            public void onControlChange(int value) {

            }

            @Override
            public void onControlStopTouch() {

            }

            @Override
            public void onControlStartTouch() {
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
