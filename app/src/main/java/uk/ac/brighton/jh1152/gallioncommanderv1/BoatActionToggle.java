package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;


public class BoatActionToggle implements BaseBoatActionUI{

    int actionPosition;
    Boat lBoat;
    int currentValue;
    String buttonTextSuffix;
    String stateNames[];
    boolean isToggled;
    Button button;

    //consider passing object in, we may beable to remove boat reference
    public BoatActionToggle(Activity activity, Boat boat, int position, int value, String buttonText, String onOffStateNames[]) {
        lBoat = boat;
        actionPosition = position;
        currentValue = value;
        stateNames = onOffStateNames;
        buttonTextSuffix = buttonText;


        button = (Button) new Button(activity);
        TableLayout layout = (TableLayout) activity.findViewById(R.id.tableLayout);
        layout.addView(button);
        setTextValue();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentValue = 0;
                SetBoatAction();
            }
        });


    }

    private void setTextValue(){
       String buttonTextPrefix = stateNames[isToggled ? 1: 0];

        String buttonText =  buttonTextPrefix + " " + buttonTextSuffix;
        button.setText((CharSequence) buttonText);
    }

    @Override
    public Boolean SetBoatAction() {
        isToggled = !isToggled;
        lBoat.setActionValue(actionPosition, currentValue);
        setTextValue();
        return isToggled;
    }
}
