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
        Intent intent = getIntent();
        gameID = intent.getStringExtra(ActivityExtras.EXTRA_BOAT_ID);
        playerNumber = intent.getIntExtra(ActivityExtras.EXTRA_PLAYER_NUMBER, -1);
        setContentView(R.layout.game_layout);
        livesLeftText = findViewById(R.id.lives_remaining_text);
        actionsRemainingText = findViewById(R.id.instructions_size_text);
        initBoat();
    }

    public void addActionButton(BoatAction action){
        UIActionController newUIAction = new UIActionController(this, boatConnector.currentBoat, action);
        boatActionButtons.put(action.documentReference, newUIAction);
    }

    public void removeAllActionButtons(){
        boatActionButtons.clear();
        LinearLayout layout = (LinearLayout) findViewById(R.id.activitiesGrid);
        layout.removeAllViews();
    }

    private void initBoat(){
        boatActionButtons = new HashMap<>();
        boatConnector = new BoatConnector(this, playerNumber);
        boatConnector.formBoatFromDocument(gameID);
    }

    public void updateUI(){

        for (Map.Entry<String, UIActionController> entry: boatActionButtons.entrySet()) {
            UIActionController UIAction = entry.getValue();
            UIAction.updateDisplay();
        }

        displayActionsLeft();
        displayLivesLeft();
    }

    private void displayActionsLeft(){
        actionsRemainingText.setText( Integer.toString(boatConnector.getActionsRemainingAmount()));
    }

    private void displayLivesLeft(){
        livesLeftText.setText(Integer.toString(boatConnector.currentBoat.livesRemaining));
    }



//    @Override
//    protected void onResume() {
//        super.onResume();
//        initBoat();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        boatConnector.destroyListeners();
//    }
//
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        boatConnector.destroyListeners();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        boatConnector.destroyListeners();
//    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LandingActivity.class);
        startActivity(intent);
    }
}
