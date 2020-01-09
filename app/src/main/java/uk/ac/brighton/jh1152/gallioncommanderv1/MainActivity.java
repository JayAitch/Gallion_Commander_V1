package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    TextView lobbyCodeDisplay;
    TextView lobbyCodeInput;
    BoatAction currentActions[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.landing_screen);
        setContentView(R.layout.game_layout);
 //       setupLobbyScreen();

        // probably on play
        db = FirebaseFirestore.getInstance();
        setupGameScreen();
        currentActions = new BoatAction[10];



    }

    void setupGameScreen(){

        createActionsFromSnapshot();
    }



    void setupLobbyScreen(){
        Button joinBtn = (Button) findViewById(R.id.joinBtn);
        Button hostBtn = (Button) findViewById(R.id.hostBtn);


        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.lobby_screen);
                lobbyCodeDisplay = (TextView) findViewById(R.id.lobbyCode);
                lobbyCodeInput = (TextView) findViewById(R.id.lobbyCodeInput);


                Button goButton = (Button) findViewById(R.id.goBtn);
                goButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        joinLobby();
                    }
                });


            }
        });

        hostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.lobby_screen);

                // dont do this here
                lobbyCodeDisplay = (TextView) findViewById(R.id.lobbyCode);
                lobbyCodeInput = (TextView) findViewById(R.id.lobbyCodeInput);
                createLobby();
            }
        });
    }



    int itterator = 0;
    Button[] button;

    void createActionsFromSnapshot(){
        final TableLayout layout = (TableLayout) findViewById(R.id.tableLayout);
        itterator = 0;
        CollectionReference actionscollection = db.collection("games/CpSBjqsXuGt9b9wdLYDi/actions");
        final AppCompatActivity content = this;





        actionscollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    currentActions = new BoatAction[task.getResult().size()];

                    for (QueryDocumentSnapshot document : task.getResult()) {


                        String name = document.get("name").toString();
                        int target = (int)(long)document.get("target");
                        int current = (int)(long)document.get("current");
                        currentActions[itterator] = new BoatAction(name, target, current, document.getId());
                        CustomButton newButton = new CustomButton(itterator, content);
                        layout.addView(newButton);
                        itterator++;
                    }


                  //  for(int i = 0;  i < currentActions.length; i++){
                     //   final Button newButton = new Button(content);
                //        newButton.setText((CharSequence) currentActions[i].actionName);
                   //     layout.addView(newButton);
                    //    newButton.setOnClickListener( new buttonclick(i));
                //    }

                } else {

                }

            }
        });




    }

    class CustomButton extends AppCompatButton {

        int actionPosition;
        buttonClick btnClick;
        public CustomButton(Context content){
            super(content);
        }

        public CustomButton(int pos, Context content){
            super(content);
            actionPosition = pos;
            this.setText(currentActions[actionPosition].actionName);
            btnClick = new buttonClick(actionPosition);
            this.setOnClickListener(btnClick);
        }
    }


    class buttonClick implements View.OnClickListener{
        int actionPosition;

        public buttonClick(int pos){
            actionPosition = pos;
        }
        @Override
        public void onClick(View v) {
            completeAction(actionPosition);
        }
    }

    void joinLobby() {
        String docCode = lobbyCodeInput.getText().toString();
        db.collection("games").document(docCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                lobbyCodeDisplay.setText(document.getId().toString());
                            } else {
                                lobbyCodeDisplay.setText("incorrect code");
                            }
                        } else {

                        }
                    }
                });
    }


    void createLobby() {
        Map<String, Object> lobby = new HashMap<>();

        //lobby.put("actions", actions);


        // Add a new document with a generated ID
        db.collection("games")
                .add(lobby)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("dfdf", "DocumentSnapshot added with ID: " + documentReference.getId());
                        lobbyCodeDisplay.setText(documentReference.getId().toString());


                        // this is going to want to be an array of actions

                        currentActions[0] = new BoatAction("raise the jolly rodger!", 1, 0, "1212d1d1d");
                        currentActions[1] = new BoatAction("walk the plank", 1, 0, "dsvsv2");

                        CollectionReference actionsCollection = db.collection("games/" + documentReference.getId() + "/actions/");

                        for(BoatAction boatAction: currentActions){
                            if(boatAction != null){
                                actionsCollection.add(boatAction.actionDBValues)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d("dfdf", "DocumentSnapshot added with ID: " + documentReference.getId());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("dfdf", "Error adding document", e);
                                            }
                                        });
                            }

                        }





                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("dfdf", "Error adding document", e);
                    }
                });


    }

// this will need to be able to set values
    void completeAction(int pos) {
        Map<String, Object> lobby = new HashMap<>();
     //   Map<String, Object> actions = new HashMap<>();

        Log.w("dfdf>>>>>>>>>>>>>>>>>>", "value inside" + pos);
        BoatAction action = currentActions[pos];
        action.actionCurrent = 0;
        String actionDocId = action.docRef;

        Log.w("dfdf>>>>>>>>>>>>>>>>>>", "docid" + actionDocId);
        //new BoatAction("walk the plank", 1, 1);
     //   actions.put("walk the plank", 1);
   //     lobby.put("actions", actions);

        db.collection("games/CpSBjqsXuGt9b9wdLYDi/actions")
                .document(actionDocId).update(action.getDocumentValues())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

    }
}
