package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;


public class BoatActionToggle implements BaseBoatActionUI{


    Boat lBoat;

    BoatAction action;
    boolean isToggled;
    Button button;
    int actionPos;

    public BoatActionToggle(Activity activity, Boat boat,int position, BoatAction boatAction) {
        lBoat = boat;
        actionPos = position;
        action = boatAction;

        button = (Button) new Button(activity);
        TableLayout layout = (TableLayout) activity.findViewById(R.id.tableLayout);
        layout.addView(button);
        setTextValue();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetBoatAction();
            }
        });
    }


    private void setTextValue(){
       String buttonTextPrefix = action.states[isToggled ? 1: 0];
       String buttonText =  buttonTextPrefix + " " + action.actionName;
       button.setText((CharSequence) buttonText);
    }

    @Override
    public Boolean SetBoatAction() {
        isToggled = !isToggled;
        lBoat.setActionValue(actionPos, action.actionCurrent);
        setTextValue();
        return isToggled;
    }
}
