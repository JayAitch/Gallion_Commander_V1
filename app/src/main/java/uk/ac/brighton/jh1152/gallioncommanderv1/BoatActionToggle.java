package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.Switch;


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

        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.activitiesGrid);
//        toggleSwitch.setShowText(true);
//        toggleSwitch.setText(boatAction.actionName);
//        toggleSwitch.setTextSize(18);
//        // create this all in xml
//        toggleSwitch.setGravity(Gravity.FILL_HORIZONTAL);
//        toggleSwitch.setTextOff(boatAction.states[1]);
//        toggleSwitch.setTextOn(boatAction.states[0]);
//       // toggleSwitch.setWidth(666);

//        toggleSwitch.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
//
//        toggleSwitch.setSwitchPadding(48);
//        toggleSwitch.setSwitchMinWidth(58);
//
//
//
//
//        toggleSwitch.setBackgroundColor(toggleSwitch.getContext().getResources().getColor(R.color.panelBackground));
//
//        toggleSwitch.setPadding(32,32,32, 32);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(24,24,24, 24);


//        params.setMargins(24, 24, 24, 24);
//
//        toggleSwitch.setLayoutParams(params);
        button.setLayoutParams(params);



        layout.addView(button);
        valueChangeCallback();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetBoatAction();
            }
        });
    }

    @Override
    public void valueChangeCallback(){
        isToggled = (action.actionCurrent == 1);
       String buttonTextPrefix = action.states[isToggled ? 0: 1];
       String buttonText =  buttonTextPrefix + " the " + action.actionName;
       button.setText((CharSequence) buttonText);
       toggleSwitch.setChecked(isToggled);
    }

    @Override
    public Boolean SetBoatAction() {
        isToggled = !isToggled;
       // lBoat.setActionValue(actionPos, action.actionCurrent);
        lBoat.setActionValue(action.documentReference, isToggled ? 1: 0);
        //valueChangeCallback();
        return isToggled;
    }
}
