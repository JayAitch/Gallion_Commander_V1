package uk.ac.brighton.jh1152.gallioncommanderv1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainGameActivity extends AppCompatActivity {
    Boat boat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("game activitive<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", "create called<<<<<<<");
        Log.w("game activitive<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", "create called<<<<<<<");
        setContentView(R.layout.game_layout);
        boat = new Boat(0, "0"); // persist this object between states
        createActionButtons(boat.getActions());
    }


    private void createActionButtons(BoatAction actions[]){
        int iterator = 0;

        for (BoatAction boatAction : actions) {
            Log.d("game activitive", "" + iterator);
            BaseBoatActionUI newUIAction = new BoatActionToggle(this, boat, iterator, boatAction.actionCurrent,  boatAction.actionName,  boatAction.states);
            iterator++;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

}
