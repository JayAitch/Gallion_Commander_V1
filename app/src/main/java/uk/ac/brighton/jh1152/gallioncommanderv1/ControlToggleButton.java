package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


public class ControlToggleButton extends LinearLayout implements ICustomControl{

    String name;
    String[] stateNames;
    TextView nameText;
    Button button;
    Boolean isToggled;

    public ControlToggleButton(Context context) {
        super(context);
    }

    public ControlToggleButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlToggleButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    // constructor called by reflection
    public ControlToggleButton(Context context, String name, String[] stateNames, int currentValue) {
        super(context);
        this.name = name;
        this.stateNames = stateNames;
        createViewObjects();
        setCurrentValue(currentValue);
    }

    private void createViewObjects(){
        LayoutInflater.from(getContext()).inflate(R.layout.action_button, this);
        nameText = (TextView) findViewById(R.id.nameText);
        button = (Button) findViewById(R.id.actionButton);
        nameText.setText(name);
    }

    @Override
    public void setCurrentValue(int statePosition){
        // only allow data to set the value
        isToggled = (statePosition != 0);
        String buttonText = stateNames[isToggled ? 0 : 1];
        button.setText(buttonText);
    }

    @Override
    public void setControlListener(final IControlListener controlListener) {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                controlListener.onControlChange(!isToggled ? 1 : 0);
                controlListener.onControlStopTouch();
            }
        });
    }

}
