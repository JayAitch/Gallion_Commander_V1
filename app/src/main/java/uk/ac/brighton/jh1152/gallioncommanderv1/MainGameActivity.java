package uk.ac.brighton.jh1152.gallioncommanderv1;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;



public class MainGameActivity extends AppCompatActivity {
    Boat boat;
    BoatConnector boatConnector;
    String gameID;// = "EiDo3HKycS8ckYxdMNGw";
    int playerNumber;

    HashMap<String, IBaseBoatActionUI> boatActionButtons;


    TextView instructionTextDisplay;
    HashMap.Entry<String, String> currentInstruction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        gameID = intent.getStringExtra(LobbyActivity.EXTRA_BOAT_ID);
        playerNumber = intent.getIntExtra(LobbyActivity.EXTRA_PLAYER_NUMBER, -1);


        setContentView(R.layout.game_layout);
        boatActionButtons = new HashMap<>();
        boatConnector = new BoatConnector(this, playerNumber);
        boatConnector.formBoatFromDocument(gameID);
    }


    public void addActionButton(BoatAction action){
        IBaseBoatActionUI newUIAction;
        if(action.states.length > 2){
            // consider not adding states at all
            newUIAction = new BoatActionSlider(this, boatConnector.currentBoat,0, action);
        }else{
            newUIAction = new BoatActionToggle(this, boatConnector.currentBoat, 0, action);
        }

        boatActionButtons.put(action.documentReference, newUIAction);
    }





    public void updateUI(){

        for (Map.Entry<String, IBaseBoatActionUI> entry: boatActionButtons.entrySet()) {
            IBaseBoatActionUI UIAction = entry.getValue();
            UIAction.setTextValue();
        }
/**
        if (currentInstruction != null && !boat.isShipComplete()) {

             if (!boat.isInstructionPossible(currentInstruction.getKey())) {
                currentInstruction = boat.getNewInstruction();
                Log.d("<<<<<<<<<<<<<<<", "instruction is" + currentInstruction.getValue());
                displayCurrentInstruction();

            }

         } else {
            currentInstruction = boat.getNewInstruction();
            displayCurrentInstruction();
        }
        displayCurrentInstruction();
 **/
    }


    private void displayCurrentInstruction(){
        if(boat.isShipComplete()){
            instructionTextDisplay.setText("complete");
        }
        else{
            if(currentInstruction != null) {
                instructionTextDisplay.setText(currentInstruction.getValue());
            }



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
