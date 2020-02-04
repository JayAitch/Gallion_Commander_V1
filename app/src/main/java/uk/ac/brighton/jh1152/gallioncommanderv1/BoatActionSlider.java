package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.SeekBar;


public class BoatActionSlider implements IBaseBoatActionUI {

    int currentValue;
    Boat lBoat;
    BoatAction action;
    NamedSeekBarWithText seekbarWithText;

    public BoatActionSlider(Activity activity, Boat boat, BoatAction boatAction) {
        lBoat = boat;
        action = boatAction;

        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.activitiesGrid);

        seekbarWithText = new NamedSeekBarWithText(activity, action.actionName, action.states, action.actionCurrent);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layout.addView(seekbarWithText);




        seekbarWithText.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentValue = progress;
                seekbarWithText.setCurrentValue(currentValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SetBoatAction();
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
    }
}
