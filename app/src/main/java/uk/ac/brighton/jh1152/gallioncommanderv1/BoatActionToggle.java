package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;


public class BoatActionToggle implements IBaseBoatActionUI {


    Boat lBoat;
    BoatAction action;
    boolean isToggled;
    Button button;
    Switch toggleSwitch;

    public BoatActionToggle(Activity activity, Boat boat, BoatAction boatAction) {
        lBoat = boat;
        action = boatAction;

        button = (Button) new Button(activity);
        toggleSwitch = (Switch) new Switch(activity);
        TableLayout layout = (TableLayout) activity.findViewById(R.id.tableLayout);
        toggleSwitch.setShowText(true);
        toggleSwitch.setText(boatAction.actionName);

        toggleSwitch.setTextOff(boatAction.states[0]);
        toggleSwitch.setTextOn(boatAction.states[1]);
        layout.addView(toggleSwitch);
        layout.addView(button);
        setTextValue();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetBoatAction();
            }
        });
        toggleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetBoatAction();
            }
        });
    }

    @Override
    public void setTextValue(){
        isToggled = (action.actionCurrent == 1);
       String buttonTextPrefix = action.states[isToggled ? 0: 1];
       String buttonText =  buttonTextPrefix + " the " + action.actionName;
       button.setText((CharSequence) buttonText);
       toggleSwitch.setChecked(isToggled);
    }

    @Override
    public Boolean SetBoatAction() {
        isToggled = !isToggled;
        Log.d("toggling", ""+ action.documentReference);
       // lBoat.setActionValue(actionPos, action.actionCurrent);
        lBoat.setActionValue(action.documentReference, isToggled ? 1: 0);
        //setTextValue();
        return isToggled;
    }
}
