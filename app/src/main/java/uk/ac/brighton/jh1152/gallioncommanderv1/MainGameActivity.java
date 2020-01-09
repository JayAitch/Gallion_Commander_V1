package uk.ac.brighton.jh1152.gallioncommanderv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainGameActivity extends AppCompatActivity {
    Boat boat;
    String gameID = "CpSBjqsXuGt9b9wdLYDi";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("game activitive<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", "create called<<<<<<<");
        Log.w("game activitive<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", "create called<<<<<<<");
        setContentView(R.layout.game_layout);
        boat = new Boat(0, gameID); // persist this object between states
        createActionButtons(boat.getActions());
    }


    private void createActionButtons(BoatAction actions[]){
        int iterator = 0;

        for (BoatAction boatAction : actions) {
            Log.d("game activitive", "" + iterator);
            BaseBoatActionUI newUIAction = new BoatActionToggle(this, boat,iterator, boatAction);
            iterator++;
        }
    }

    private void GetActions(){
        CollectionReference actionscollection = db.collection("games/CpSBjqsXuGt9b9wdLYDi/actions");
        actionscollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    currentActions = new BoatAction[task.getResult().size()];

                    for (QueryDocumentSnapshot document : task.getResult()) {


                        String name = document.get("name").toString();
                        int target = (int)(long)document.get("target");
                        int current = (int)(long)document.get("current");
                        //          currentActions[itterator] = new BoatAction(name, target, current, document.getId());
                        MainActivity.CustomButton newButton = new MainActivity.CustomButton(itterator, content);
                        layout.addView(newButton);
                        itterator++;
                    }

                } else {

                }

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
