package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.SeekBar;


public class BoatActionMultiState implements IBaseBoatActionUI {

    int currentValue;
    Boat lBoat;
    BoatAction action;
    ControlSeekBarWithText seekbarWithText; // make generic should be able to have any multivalue actio on here
    ControlKnob testKnob; //temp



    public BoatActionMultiState(Activity activity, Boat boat, BoatAction boatAction) {
        lBoat = boat;
        action = boatAction;

        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.activitiesGrid);

        seekbarWithText = new ControlSeekBarWithText(activity, action.actionName, action.states, action.actionCurrent);

        layout.addView(seekbarWithText);

//temp


        testKnob = new ControlKnob(activity, action.actionName, action.states, action.actionCurrent);
        layout.addView(testKnob);
        testKnob.setControlListener(new IControlListener() {
            @Override
            public void onControlChange(int value) {

            }

            @Override
            public void onControlStopTouch() {

            }

            @Override
            public void onControlStartTouch() {

            }
        });


        seekbarWithText.setControlListener(new IControlListener() {
            @Override
            public void onControlChange(int value) {
                currentValue = value;
                seekbarWithText.setCurrentValue(currentValue);
            }

            @Override
            public void onControlStopTouch() {
                SetBoatAction();
            }

            @Override
            public void onControlStartTouch() {

            }
        });
    }


    @Override
    public Boolean SetBoatAction() {
              lBoat.setActionValue(action.documentReference, currentValue);
        return null;
    }

    @Override
    public void valueChangeCallback() {

        seekbarWithText.setCurrentValue(action.actionCurrent);
        testKnob.setCurrentValue(action.actionCurrent);
    }
}
