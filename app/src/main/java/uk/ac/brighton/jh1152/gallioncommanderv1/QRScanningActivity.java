package uk.ac.brighton.jh1152.gallioncommanderv1;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScanningActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create new scanner and set it as the content
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    // callback from result handler repond to QR code read
    @Override
    public void handleResult(Result result) {
        String qrCodeText = result.getText();
        // really simple check, could use a regular expression instead
        if(qrCodeText.length() == 20)
        {
            launchLobbyActivity(qrCodeText);
        }
        else{
            onResume();
            Toast toast = Toast.makeText(this, "Incorrect QR code, scan a lobby code", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // make sure scanner exists and set the content to it when resuming
    @Override
    protected void onResume() {
        super.onResume();
        if(mScannerView == null){
            mScannerView = new ZXingScannerView(this);
            setContentView(mScannerView);
        }
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    // cleanup scanner if the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }

    // launch lobby activity with the id found
    private void launchLobbyActivity(String documentID){
        Intent intent = new Intent(this, LobbyActivity.class);
        String boatIdMessage = documentID;
        intent.putExtra(ActivityExtras.EXTRA_LOBBY_ID, boatIdMessage);
        startActivity(intent);
    }


}
