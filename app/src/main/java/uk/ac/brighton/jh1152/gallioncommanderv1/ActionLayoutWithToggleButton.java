package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;


public class ActionLayoutWithToggleButton extends LinearLayout {

    String name;
    String[] stateNames;
    TextView nameText;
    Button button;

    public ActionLayoutWithToggleButton(Context context) {
        super(context);
    }

    public ActionLayoutWithToggleButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionLayoutWithToggleButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    public ActionLayoutWithToggleButton(Context context, String name, String[] stateNames, int currentValue) {
        super(context);
        this.name = name;
        this.stateNames = stateNames;
        createViewObjects();
        setCurrentValue(currentValue);
    }

    private void createViewObjects(){
        LayoutInflater.from(getContext()).inflate(R.layout.action_button, this);
        nameText = (TextView) findViewById(R.id.nameText);
        button =(Button) findViewById(R.id.actionButton);
        nameText.setText(name);
    }

    public void setCurrentValue(int statePosition){
        button.setText(stateNames[statePosition]);
    }

    public void setOnClickListener(OnClickListener listener){
        button.setOnClickListener(listener);
    }
}
