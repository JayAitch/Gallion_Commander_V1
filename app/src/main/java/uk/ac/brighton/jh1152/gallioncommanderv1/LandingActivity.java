package uk.ac.brighton.jh1152.gallioncommanderv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class LandingActivity extends AppCompatActivity {
    EditText lobbyIDEntry;
    Button hostBtn;
    Button joinBtn;
    Button scanBtn;
    FirebaseFirestore db;
    public static final String EXTRA_LOBBY_ID = "uk.ac.brighton.jh1152.gallioncommanderv1.LOBBYID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_screen);
        lobbyIDEntry = findViewById(R.id.lobby_id_text);
        hostBtn = findViewById(R.id.hostBtn);
        joinBtn = findViewById(R.id.joinBtn);
        scanBtn = findViewById(R.id.scan_btn);
        createButtonEvents();
        db = FirebaseFirestore.getInstance();
    }

    private void createButtonEvents(){
        hostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hostLobby();
            }
        });
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinLobby();
            }
        });
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testQRSCan();
            }
        });
    }


    private void joinLobby(){

        db.collection("games").document(getGameName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        launchLobbyActivity(task.getResult().getId());
                    }
                });

    }



    private String getGameName(){
        String lobbyName;
        //lobbyName = "gfTR8hYzE8nGaLVyI2Lh";
        lobbyName =lobbyIDEntry.getText().toString();
        return lobbyName;
    }

    private void hostLobby(){
        Map<String, Object> newLobby = new HashMap<>();
        newLobby.put("players", 0);
        db.collection("games").add(newLobby).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                launchLobbyActivity(documentReference.getId());
            }
        });


    }

    private void launchLobbyActivity(String documentID){

        Intent intent = new Intent(this, LobbyActivity.class);
        String boatIdMessage = documentID;//"rJjFieQQOrE82iW0Wkio";//lobbyID;
        intent.putExtra(EXTRA_LOBBY_ID, boatIdMessage);
        startActivity(intent);
    }




    private void testQRSCan(){

    }
}
