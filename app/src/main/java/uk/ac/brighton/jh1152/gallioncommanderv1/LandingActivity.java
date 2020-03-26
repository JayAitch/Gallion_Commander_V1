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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

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
                launchScanningActivity();
            }
        });
    }




    private void launchScanningActivity(){
        Intent intent = new Intent(this, QRScanningActivity.class);
        startActivity(intent);
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
        String boatIdMessage = documentID;
        intent.putExtra(ActivityExtras.EXTRA_LOBBY_ID, boatIdMessage);
        startActivity(intent);
    }

}
