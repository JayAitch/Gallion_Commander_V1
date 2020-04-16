package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.HashMap;
import java.util.Map;


public class LobbyActivity extends AppCompatActivity {

    String lobbyID;
    FirebaseFirestore db;
    TextView lobbySizeText;
    TextView lobbyCodeText;
    ImageView lobbyQRCode;
    Button startGameBtn;
    DocumentReference lobbyDocument;
    ListenerRegistration lobbyListener;
    Map<String, Object> lobbyData;
    ActionCreator actionCreator;
    int thisPlayerNumber;
    String newBoatReferenceID;
    BoatAction[] boatActions;
    int boatActivityCount = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        lobbyID = intent.getStringExtra(ActivityExtras.EXTRA_LOBBY_ID);

        setContentView(R.layout.lobby_layout);
        lobbyCodeText = (TextView) findViewById(R.id.lobbyIDText);
        lobbySizeText = (TextView) findViewById(R.id.lobbySizeText);
        lobbyQRCode = (ImageView) findViewById(R.id.lobby_qr_code);
        startGameBtn = (Button) findViewById(R.id.start_game_button);
        db = FirebaseFirestore.getInstance();
        lobbyDocument = db.collection(DocumentLocations.LOBBY_COLLECTION).document(lobbyID);
        actionCreator = new ActionCreator(this);

        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewBoatAndSetReference();
            }
        });
    }


    // query lobby document and generate display
    private void connnectToLobby(){
        lobbyDocument.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                lobbyCodeText.setText(document.getId().toString());

                                lobbyData = document.getData();
                                lobbyData.remove("boat");
                                // asyncronously increment field value
                                lobbyData.put("players", FieldValue.increment(1));
                                // commit lobby document cahnges to database
                                lobbyDocument.update(lobbyData);
                                // display lobby QR code
                                generateAndDisplayQRCode(document.getId());
                                // keep track of player position
                                thisPlayerNumber = document.get("players", Integer.class);


                            } else {
                                lobbyCodeText.setText("failed to connect to lobby");

                            }
                        } else {

                        }
                    }
                });

        // add a listener to the document for player size changes
        lobbyListener = lobbyDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                int players = documentSnapshot.get("players", Integer.class);
                lobbySizeText.setText(String.valueOf(players));
                lobbyData = documentSnapshot.getData();

                String boatID = documentSnapshot.get("boat", String.class);

                // if the boat has been added to the document trigger game activity
                if( boatID!= null && boatID.length() == 20){
                    lobbyListener.remove();
                    launchGame(boatID);
                }
            }
        });
    }


    // build intent to pass boat document id and launch maingame acvitiy
    private void launchGame(String boatID){
        Intent intent = new Intent(this, MainGameActivity.class);
        String boatIdMessage = boatID;
        intent.putExtra(ActivityExtras.EXTRA_BOAT_ID, boatIdMessage);
        intent.putExtra(ActivityExtras.EXTRA_PLAYER_NUMBER, thisPlayerNumber);
        startActivity(intent);
    }


    // keep track of the players that are connected, clean up listners
    private void disconnectFromLobby(){
        lobbyData.put("players", FieldValue.increment(-1));
        lobbyDocument.update(lobbyData);
        lobbyListener.remove();
    }

    // respond to activity lifecyles
    @Override
    protected void onPause() {
        super.onPause();
        disconnectFromLobby();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connnectToLobby();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectFromLobby();
    }

    private BoatAction[] createBoatActionsCollection(){
        return  actionCreator.getRandomActions(GameSettings.BASE_FINISHED_ACTIVITIES,GameSettings.BASE_UNFINISHED_ACTIVITIES);
    }


    // generate a new boat object with a set of actions and commit to the database
    private void createNewBoatAndSetReference(){
        Map<String, Object> newBoat = new HashMap<>();
        newBoat.put("lives", GameSettings.BASE_BOAT_LIVES);
        newBoat.put("players", lobbyData.get("players"));
        // create map of actions
        boatActions = createBoatActionsCollection();

        db.collection("boats").add(newBoat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                newBoatReferenceID = documentReference.getId();
                // build document location string
                String boatLocation = DocumentLocations.BOAT_COLLECTION + "/"+ newBoatReferenceID + "/" + DocumentLocations.ACTION_COLLECTION;
                for(BoatAction action: boatActions){

                    // create new tasks against the boat document
                    db.collection(boatLocation).document(action.documentReference).set(action.getDocumentValues()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            boatActivityCount++;
                            // make sure this has finished commiting to the database before adding boat reference to lobby document
                            checkIfFinishedCreatingActions(boatActivityCount);
                        }
                    });
                }

            }
        });

    }


    // display the current lobby as a QR and text
    private void generateAndDisplayQRCode(String code)
    {
        Bitmap qrCodeBitmap;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            // utilise barcode encoder to generate QR bitmap
            BitMatrix bitMatrix = multiFormatWriter.encode(code, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            qrCodeBitmap = barcodeEncoder.createBitmap(bitMatrix);
            lobbyQRCode.setImageBitmap(qrCodeBitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }


    }

    // make sure actions have finished comiting before triggering actibity changes
    private void checkIfFinishedCreatingActions(int actionCount){
        if(boatActions.length == actionCount){
            lobbyData.put("boat", newBoatReferenceID);
            lobbyDocument.update(lobbyData);
        }
    }


}



