package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class NamedSeekBarWithText extends LinearLayout {

    String name;
    String[] stateNames;
    TextView nameText;
    TextView stateText;
    SeekBar seekBar;


    public NamedSeekBarWithText(Context context) {
        super(context);
    }


    public NamedSeekBarWithText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NamedSeekBarWithText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NamedSeekBarWithText(Context context, String name, String[] stateNames, int currentValue) {
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

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener){
        seekBar.setOnSeekBarChangeListener(listener);
    }

}
