package uk.ac.brighton.jh1152.gallioncommanderv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LandingActivity extends AppCompatActivity {
    EditText lobbyIDEntry;
    Button hostBtn;
    Button joinBtn;
    Button scanBtn;
    FirebaseFirestore db;
    CameraSource cameraSource;
    SurfaceView cameraView;
    BarcodeDetector detector;
    public static final String EXTRA_LOBBY_ID = "uk.ac.brighton.jh1152.gallioncommanderv1.LOBBYID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_screen);
        lobbyIDEntry = findViewById(R.id.lobby_id_text);
        hostBtn = findViewById(R.id.hostBtn);
        joinBtn = findViewById(R.id.joinBtn);
        scanBtn = findViewById(R.id.scan_btn);
        cameraView = (SurfaceView) findViewById(R.id.camera_preview);
        createButtonEvents();
        loadCamerAndScanner();
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
    Bitmap testBitmap;

    private void loadCamerAndScanner(){
        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this,detector)
                .setRequestedPreviewSize(640,480)
                .build();

       // ImageView testScanImage = (ImageView) findViewById(R.id.testScanImage);
      //  testBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.bb74793e6be9106b);
       // testScanImage.setImageBitmap(testBitmap);
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





        if(!detector.isOperational()){
            lobbyIDEntry.setText("barcode failed to set up");
            return;
        }
        else{
            lobbyIDEntry.setText("scanning");


            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });

            detector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if(barcodes.size() != 0){
                        Barcode thisCode = barcodes.valueAt(0);
                        lobbyIDEntry.setText(thisCode.rawValue);
                        detector.release();
                    }
                }
            });

        }

    }
}
