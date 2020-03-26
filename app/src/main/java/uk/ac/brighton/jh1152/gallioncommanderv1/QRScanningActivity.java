package uk.ac.brighton.jh1152.gallioncommanderv1;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScanningActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

    }

    @Override
    public void handleResult(Result result) {
        String qrCodeText = result.getText();
        launchLobbyActivity(qrCodeText);
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }


    private void launchLobbyActivity(String documentID){
        Intent intent = new Intent(this, LobbyActivity.class);
        String boatIdMessage = documentID;
        intent.putExtra(ActivityExtras.EXTRA_LOBBY_ID, boatIdMessage);
        startActivity(intent);
    }


}
