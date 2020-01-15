package uk.ac.brighton.jh1152.gallioncommanderv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;


public class LobbyActivity extends AppCompatActivity {

    public String lobbyID;
    FirebaseFirestore db;
    TextView lobbySizeText;
    TextView lobbyPositionText;
    TextView lobbyCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_layout);
        lobbyCodeText = (TextView) findViewById(R.id.lobbyIDText);
        lobbyPositionText = (TextView) findViewById(R.id.lobbyPositionText);
        lobbySizeText = (TextView) findViewById(R.id.lobbySizeText);
    }


    private void connnectToLobby(){
        db.collection("games").document(lobbyID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                lobbyCodeText.setText(document.getId().toString());
                            } else {
                                lobbyCodeText.setText("incorrect code");
                            }
                        } else {

                        }
                    }
                });

    }
}
