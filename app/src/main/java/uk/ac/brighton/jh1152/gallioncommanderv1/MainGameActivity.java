package uk.ac.brighton.jh1152.gallioncommanderv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainGameActivity extends AppCompatActivity {
    Boat boat;
    String gameID = "EiDo3HKycS8ckYxdMNGw";
    FirebaseFirestore db;
    HashMap<String, BoatAction> activities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.game_layout);
        GetActions();
        //boat = new Boat(0, gameID); // persist this object between states
        //createActionButtons(boat.getActions());
    }


    private void createActionButtons(BoatAction actions[]){
        int iterator = 0;

        for (BoatAction boatAction : actions) {
            Log.d("game activitive", "" + iterator);
            BaseBoatActionUI newUIAction = new BoatActionToggle(this, boat, iterator, boatAction);
            iterator++;
        }
    }


    private void createActionButtons(HashMap<String,BoatAction> actions){
        int iterator = 0;

        for (Map.Entry<String, BoatAction> entry: actions.entrySet()) {
            Log.d("game activitive", "" + iterator);

            BaseBoatActionUI newUIAction = new BoatActionToggle(this, boat, iterator, entry.getValue());
            iterator++;
        }
    }

    private void GetActions(){
        /// probably waant a single object with exlusive db access
        CollectionReference actionscollection = db.collection("boats/EiDo3HKycS8ckYxdMNGw/activities");

        activities = new HashMap<>();
        actionscollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String activityKey = (String) document.get("name");
                        int target = (int)(long) document.get("target");
                        int current = (int)(long) document.get("current");
                       // String[] states = (ArrayList<>) document.get("states");
                        String[] states = {"off", "on"};
                        BoatAction tempAction = new BoatAction(activityKey,target,current,document.getId(),states);
                        activities.put(document.getId(),tempAction);
                    }

                } else {

                }
                boat = new Boat(activities, gameID);
                createActionButtons(activities);
            }
        });
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
