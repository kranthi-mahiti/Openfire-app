package com.sanjay.openfire.views.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sanjay.openfire.R;
import com.sanjay.openfire.service.XMPP;
import com.sanjay.openfire.utilies.InternetConnection;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

public class RegistationActivity extends AppCompatActivity {

    EditText e1, e2;
    Button b1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registation);

        e1 = findViewById(R.id.username);
        e2 = findViewById(R.id.password);
        b1 = findViewById(R.id.email_sign_in_button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = e1.getText().toString();
                String password = e2.getText().toString();
                InternetConnection internetConnection = new InternetConnection(getApplicationContext());

                if (internetConnection.isNetworkAvailable()) {
                    try {
                        boolean registerSucess = XMPP.getInstance().register(username, password);
                        if (registerSucess)
                            Toast.makeText(RegistationActivity.this, username + " registerd", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistationActivity.this, ChatActivity.class));
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplication(), "Connectivity problem", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}
