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
//consider passing object in, we may beable to remove boat reference
    public BoatActionToggle(Activity activity, Boat boat, int position, int value, CharSequence buttonText) {
        lBoat = boat;
        actionPosition = position;
        currentValue = value;
        Button button = new Button(activity);
        TableLayout layout = (TableLayout) activity.findViewById(R.id.tableLayout);
        layout.addView(button);
        button.setText(buttonText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentValue = 0;
                SetBoatAction();
            }
        });
    }


    @Override
    public Boolean SetBoatAction() {
        lBoat.setActionValue(actionPosition, currentValue);
        return false;
    }
}
