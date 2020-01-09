package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TableLayout;

public class BoatActionSlider implements IBaseBoatActionUI {

    int currentValue;
    Boat lBoat;
    BoatAction action;
    SeekBar slider;
    int actionPos;

    public BoatActionSlider(Activity activity, Boat boat, final int position, BoatAction boatAction) {
        lBoat = boat;
        actionPos = position;
        action = boatAction;


        TableLayout layout = (TableLayout) activity.findViewById(R.id.tableLayout);
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
