package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;


public class GameActivity extends Activity {

    Boat boat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.d("game activitive<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", "create called<<<<<<<");
        Log.w("game activitive<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", "create called<<<<<<<");
        setContentView(R.layout.game_layout);
        //boat = new Boat(0, "0"); // persist this object between states
       // createActionButtons(boat.getActions());
    }


    private void createActionButtons(BoatAction actions[]){
        int iterator = 0;

        for (BoatAction boatAction : actions) {
            Log.d("game activitive", "" + iterator);
            BaseBoatActionUI newUIAction = new BoatActionToggle(this, boat, iterator, boatAction.actionCurrent, (CharSequence) "dfgfd");
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

