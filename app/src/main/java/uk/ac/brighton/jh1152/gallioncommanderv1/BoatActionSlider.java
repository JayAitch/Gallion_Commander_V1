package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class BoatActionSlider implements IBaseBoatActionUI {

    int currentValue;
    Boat lBoat;
    BoatAction action;
    SeekBar seekBar;
    TextView seekBarText;
    TextView seekBarActionName;


    public BoatActionSlider(Activity activity, Boat boat, BoatAction boatAction) {
        lBoat = boat;

        action = boatAction;

        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.activitiesGrid);


  ///      LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 ///      View seekBarView = (View) inflater.inflate(R.layout.action_slider_, null);
   ///     layout.addView(seekBarView);

        LinearLayout innerLayout = new LinearLayout(activity);


        seekBar = new ActionSeekBar(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        params.setMargins(24,24,24, 24);
        innerLayout.setLayoutParams(params);
        seekBar.setLayoutParams(params);


        seekBarActionName  = new TextView(activity);
        seekBarActionName.setText(action.actionName);
        seekBarActionName.setTypeface(null, Typeface.BOLD);
        seekBarActionName.setLayoutParams(params);

        seekBarText = new TextView(activity);
        seekBarText.setText(action.states[currentValue]);
        seekBarText.setGravity(Gravity.CENTER);
        seekBar.setMax(action.states.length - 1);
        layout.addView(innerLayout);




        innerLayout.addView(seekBarActionName);
        innerLayout.addView(seekBar);
        innerLayout.addView(seekBarText);
        valueChangeCallback();









        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentValue = progress;
                seekBarText.setText(action.states[currentValue]);
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


    private void inflateSeekabar(){

    }



    @Override
    public Boolean SetBoatAction() {
              lBoat.setActionValue(action.documentReference, currentValue);
        return null;
    }

    @Override
    public void valueChangeCallback() {
        seekBar.setProgress(action.actionCurrent);
       seekBarText.setText(action.states[action.actionCurrent]);
    }
}
