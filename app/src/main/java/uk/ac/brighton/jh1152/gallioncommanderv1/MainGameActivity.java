package uk.ac.brighton.jh1152.gallioncommanderv1;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;



public class MainGameActivity extends AppCompatActivity {
    BoatConnector boatConnector;
    String gameID;// = "EiDo3HKycS8ckYxdMNGw";
    int playerNumber;

    HashMap<String, IBaseBoatActionUI> boatActionButtons;


    TextView instructionTextDisplay;
    TextView livesLeftText;
    TextView actionsRemainingText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent = getIntent();
        gameID = intent.getStringExtra(LobbyActivity.EXTRA_BOAT_ID);
        playerNumber = intent.getIntExtra(LobbyActivity.EXTRA_PLAYER_NUMBER, -1);
        setContentView(R.layout.game_layout);
        instructionTextDisplay = (TextView) findViewById(R.id.instruction_text);
        livesLeftText = findViewById(R.id.lives_remaining_text);
        actionsRemainingText = findViewById(R.id.instructions_size_text);


        boatActionButtons = new HashMap<>();
        boatConnector = new BoatConnector(this, playerNumber);
        boatConnector.formBoatFromDocument(gameID);
    }

    public void addActionButton(BoatAction action){
        IBaseBoatActionUI newUIAction;

        // concider switching this to an enum
   //     if(action.states.length > 2){
            // consider not adding states at all
            newUIAction = new BoatActionMultiState(this, boatConnector.currentBoat, action);
   //     }else{
     //       newUIAction = new BoatActionToggle(this, boatConnector.currentBoat,  action);
//        }

        boatActionButtons.put(action.documentReference, newUIAction);
    }

    public void removeAllActionButtons(){
        boatActionButtons.clear();
        Log.d("removing actions buttons <<<<, ", "" + boatActionButtons.size());
        LinearLayout layout = (LinearLayout) findViewById(R.id.activitiesGrid);
        layout.removeAllViews();

    }


    // change this to a push update on a UI object
    public void updateUI(){

        for (Map.Entry<String, IBaseBoatActionUI> entry: boatActionButtons.entrySet()) {
            IBaseBoatActionUI UIAction = entry.getValue();
            UIAction.valueChangeCallback();
        }

        displayActionsLeft();
        displayLivesLeft();
    }

    public void displayActionsLeft(){
        actionsRemainingText.setText( Integer.toString(boatConnector.getActionsRemainingAmount()));
    }

    public void displayLivesLeft(){
        livesLeftText.setText(Integer.toString(boatConnector.currentBoat.livesRemaining));
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

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, LandingActivity.class);
        startActivity(intent);
    }
}
