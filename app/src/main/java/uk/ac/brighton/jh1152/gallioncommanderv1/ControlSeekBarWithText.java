package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class ControlSeekBarWithText extends LinearLayout implements  ICustomControl{

    String name;
    String[] stateNames;
    TextView nameText;
    TextView stateText;
    SeekBar seekBar;


    public ControlSeekBarWithText(Context context) {
        super(context);
    }


    public ControlSeekBarWithText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlSeekBarWithText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ControlSeekBarWithText(Context context, String name, String[] stateNames, int currentValue) {
        super(context);
        this.name = name;
        this.stateNames = stateNames;
        createViewObjects();
        setCurrentValue(currentValue);
    }

    private void createViewObjects(){
        LayoutInflater.from(getContext()).inflate(R.layout.action_seek_bar, this);
        nameText = (TextView) findViewById(R.id.nameText);
        stateText = (TextView) findViewById(R.id.stateText);
        seekBar =(SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(stateNames.length - 1);
        nameText.setText(name);
    }

    public void setCurrentValue(int statePosition){
        stateText.setText(stateNames[statePosition]);
        seekBar.setProgress(statePosition);
    }

    @Override
    public void setControlListener(final IControlListener controlListener) {


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                controlListener.onControlChange(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                controlListener.onControlStartTouch();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                controlListener.onControlStopTouch();
            }
        });
    }

}
