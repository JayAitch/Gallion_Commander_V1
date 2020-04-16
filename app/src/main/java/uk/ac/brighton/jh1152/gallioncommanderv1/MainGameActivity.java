package uk.ac.brighton.jh1152.gallioncommanderv1;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;



public class MainGameActivity extends AppCompatActivity {
    BoatConnector boatConnector;
    String gameID;
    int playerNumber;

    HashMap<String, UIActionController> boatActionButtons;
    TextView livesLeftText;
    TextView actionsRemainingText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // find current player and boat from intent parcel
        Intent intent = getIntent();
        gameID = intent.getStringExtra(ActivityExtras.EXTRA_BOAT_ID);
        playerNumber = intent.getIntExtra(ActivityExtras.EXTRA_PLAYER_NUMBER, -1);

        // set layout display and query for text objects
        setContentView(R.layout.game_layout);
        livesLeftText = findViewById(R.id.lives_remaining_text);
        actionsRemainingText = findViewById(R.id.instructions_size_text);
        // trigger load
        //initBoat();
    }

    // create a new action button and add it to the container
    public void addActionButton(BoatAction action){
        UIActionController newUIAction = new UIActionController(this, boatConnector.currentBoat, action);
        boatActionButtons.put(action.documentReference, newUIAction);
    }

    // remove all action buttons currently on the interface
    public void removeAllActionButtons(){
        boatActionButtons.clear();
        LinearLayout layout = (LinearLayout) findViewById(R.id.activitiesGrid);
        layout.removeAllViews();
    }

    // trigger boat loading
    private void initBoat(){
        boatActionButtons = new HashMap<>();
        boatConnector = new BoatConnector(this, playerNumber);
        boatConnector.formBoatFromDocument(gameID);
    }

    // display any changes to UI objects
    public void updateUI(){

        for (Map.Entry<String, UIActionController> entry: boatActionButtons.entrySet()) {
            UIActionController UIAction = entry.getValue();
            UIAction.updateDisplay();
        }

        displayActionsLeft();
        displayLivesLeft();
    }

    // display the amount of actions left before the level is complete
    private void displayActionsLeft(){
        actionsRemainingText.setText( Integer.toString(boatConnector.getActionsRemainingAmount()));
    }
    // display the amount of lives remaining
    private void displayLivesLeft(){
        livesLeftText.setText(Integer.toString(boatConnector.currentBoat.livesRemaining));
    }



    @Override
    protected void onResume() {
        super.onResume();
        initBoat();
    }

    @Override
    protected void onPause() {
        super.onPause();
        boatConnector.destroyListeners();
    }


    @Override
    protected void onStop() {
        super.onStop();
        boatConnector.destroyListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boatConnector.destroyListeners();
    }

    // override the back button to prevent bouncing between lobby and game activity from pressing back
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LandingActivity.class);
        startActivity(intent);
    }
}
