package uk.ac.brighton.jh1152.gallioncommanderv1;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LandingActivity extends AppCompatActivity {
    Button hostBtn;
    Button joinBtn;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_screen);
        hostBtn = findViewById(R.id.hostBtn);
        joinBtn = findViewById(R.id.joinBtn);
        createButtonEvents();
        checkCameraPermissions();
        db = FirebaseFirestore.getInstance();
    }

    private void checkCameraPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                //
            } else {
                // request the permissing
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        0);
            }
        } else {
            // app already has permission
        }

    }


    // build button callbacks
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
                launchScanningActivity();
            }
        });
    }



    // launch the QR scanning activity
    private void launchScanningActivity(){
        Intent intent = new Intent(this, QRScanningActivity.class);
        startActivity(intent);
    }

    // build lobby document and trigger lobby activity
    private void hostLobby(){
        Map<String, Object> newLobby = new HashMap<>();
        newLobby.put("players", 0);
        // query to create a new lobby document
        db.collection(DocumentLocations.LOBBY_COLLECTION).add(newLobby).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // trigger lobby activity after making database changes
                launchLobbyActivity(documentReference.getId());
            }
        });

    }

    // launch the lobby activity with the new documentID
    private void launchLobbyActivity(String documentID){

        Intent intent = new Intent(this, LobbyActivity.class);
        String boatIdMessage = documentID;
        intent.putExtra(ActivityExtras.EXTRA_LOBBY_ID, boatIdMessage);
        startActivity(intent);
    }

}
