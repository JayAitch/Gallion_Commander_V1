package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ControlKnob extends LinearLayout implements ICustomControl{

    ImageView controlKnob;
    TextView nameText;
    String name;
    String[] stateNames;

    public ControlKnob(Context context) {
        super(context);
    }

    public ControlKnob(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlKnob(Context context, @Nullable AttributeSet attrs, int defStyleAttr, String name, String[] stateNames, int currentValue) {
        super(context, attrs, defStyleAttr);

    }

    public ControlKnob(Context context, String name, String[] stateNames, int currentValue) {
        super(context);        this.name = name;
        this.stateNames = stateNames;
        createViewObjects();
        setCurrentValue(currentValue);

    }


    private void createViewObjects(){
        LayoutInflater.from(getContext()).inflate(R.layout.control_knob, this);
        nameText = (TextView) findViewById(R.id.nameText);
        //stateText = (TextView) findViewById(R.id.stateText);
        controlKnob =(ImageView) findViewById(R.id.control_knob);


    }

    private void setRotation(int currentValue){
        float progress = ((float)currentValue/(float)stateNames.length);
        float angle =  progress * 360;
        controlKnob.setRotation(angle);
    }

    public void setCurrentValue(int statePosition){
    //    stateText.setText(stateNames[statePosition]);
    //    seekBar.setProgress(statePosition);
        setRotation(statePosition);
    }

    @Override
    public void setControlListener(IControlListener controlListener) {

    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener){
    //    seekBar.setOnSeekBarChangeListener(listener);
    }
}
