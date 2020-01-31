package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;

public class BoatActionSlider implements IBaseBoatActionUI {

    int currentValue;
    Boat lBoat;
    BoatAction action;
    SeekBar slider;


    public BoatActionSlider(Activity activity, Boat boat, BoatAction boatAction) {
        lBoat = boat;

        action = boatAction;


        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.activitiesGrid);
        slider = new SeekBar(activity);
        layout.addView(slider);
        setTextValue();

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float intervals = 1;
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
    public void setTextValue() {

    }
}
