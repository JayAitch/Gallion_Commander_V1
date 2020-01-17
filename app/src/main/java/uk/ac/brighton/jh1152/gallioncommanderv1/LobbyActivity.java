package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class LobbyActivity extends AppCompatActivity {

    public String lobbyID ="rJjFieQQOrE82iW0Wkio";
    FirebaseFirestore db;
    TextView lobbySizeText;
    TextView lobbyPositionText;
    TextView lobbyCodeText;
    ImageView lobbyQRCode;
    Button startGameBtn;
    DocumentReference lobbyDocument;
    Map<String, Object> lobbyData;
    WriteBatch activitiesBatch;
    int thisPlayerNumber;
    public static final String EXTRA_BOAT_ID = "uk.ac.brighton.jh1152.gallioncommanderv1.BOATID";
    //public static final String EXTRA_PLAYER_NUMBER = "uk.ac.brighton.jh1152.gallioncommanderv1.PLAYERS";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        lobbyID = intent.getStringExtra(LandingActivity.EXTRA_LOBBY_ID);

        setContentView(R.layout.lobby_layout);
        lobbyCodeText = (TextView) findViewById(R.id.lobbyIDText);
        lobbyPositionText = (TextView) findViewById(R.id.lobbyPositionText);
        lobbySizeText = (TextView) findViewById(R.id.lobbySizeText);
        lobbyQRCode = (ImageView) findViewById(R.id.lobby_qr_code);
        startGameBtn = (Button) findViewById(R.id.start_game_button);
        db = FirebaseFirestore.getInstance();
        lobbyDocument = db.collection("games").document(lobbyID);


        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewBoatAndSetReference();
            }
        });
    }


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
                                lobbyData.put("players", FieldValue.increment(1));
                                lobbyDocument.update(lobbyData);
                                generateAndDisplayQRCode(document.getId());

                                thisPlayerNumber = document.get("players", Integer.class);
                                lobbyPositionText.setText(String.valueOf(thisPlayerNumber));

                            } else {
                                lobbyCodeText.setText("failed to connect to lobby");

                            }
                        } else {

                        }
                    }
                });

        lobbyDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                int players =(int)(long)documentSnapshot.get("players");
                lobbySizeText.setText(String.valueOf(players));



                String boatID = documentSnapshot.get("boat", String.class);

                if( boatID!= null && boatID.length() == 20){
                    launchGame(boatID);
                }
            }
        });
    }



    private void launchGame(String boatID){
        Log.d("game>>>>>>> ", "game launched");
        Intent intent = new Intent(this, MainGameActivity.class);
        String boatIdMessage = boatID;
        intent.putExtra(EXTRA_BOAT_ID, boatIdMessage);
        //intent.putExtra(EXTRA_PLAYER_NUMBER, thisPlayerNumber);
        startActivity(intent);
    }



    private void disconnectFromLobby(){
        lobbyData.put("players", FieldValue.increment(-1));
        lobbyDocument.update(lobbyData);
    }

    @Override
    protected void onPause() {
        super.onPause();
     //   disconnectFromLobby();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connnectToLobby();
    }

    private BoatAction[] CreateBoatActionsCollection(){
        BoatAction[] newBoatActins = new BoatAction[4];
        String[] tempStates = {"release","capture"};
        newBoatActins[0] = new BoatAction("Kraken", 0, 1, "0", Arrays.copyOf(tempStates, tempStates.length));



        String[] tempStates2 = {"unload","load"};
        newBoatActins[1] = new BoatAction("Cannons", 0, 1, "1", Arrays.copyOf(tempStates2, tempStates2.length));


        String[] tempStates3 = {"raise","lower"};
        newBoatActins[2] = new BoatAction("Jolly Rodger", 0, 1, "2", Arrays.copyOf(tempStates3, tempStates3.length));


        String[] tempStates4 = {"down","up"};
        newBoatActins[3] = new BoatAction("Rudder", 0, 1, "3", Arrays.copyOf(tempStates4, tempStates4.length));
        return newBoatActins;
    }








    String newBoatReferenceID;
    BoatAction[] boatActions = CreateBoatActionsCollection();
    int BoatActivtyCount= 0;
    private void createNewBoatAndSetReference(){
        Map<String, Object> newBoat = new HashMap<>();
        newBoat.put("lives", 10);



        db.collection("boats").add(newBoat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                newBoatReferenceID = documentReference.getId();

                for(BoatAction action: boatActions){

                    db.collection("boats/"+ newBoatReferenceID +"/activities").add(action.getDocumentValues()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("<<<<<<<<<<<<<<<<<<<", "created new boat referece" + newBoatReferenceID);
                            BoatActivtyCount++;
                            CheckIfFinishedCreatingActions(BoatActivtyCount);

                        }
                    });
                }

            }
        });

    }


    private void generateAndDisplayQRCode(String code)
    {
        Bitmap qrCodeBitmap;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(code, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            qrCodeBitmap = barcodeEncoder.createBitmap(bitMatrix);
            lobbyQRCode.setImageBitmap(qrCodeBitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }


    }
    private void CheckIfFinishedCreatingActions(int activitesCount){
        if(boatActions.length == activitesCount){
            lobbyData.put("boat", newBoatReferenceID);
            lobbyDocument.update(lobbyData);
        }

    }


}



