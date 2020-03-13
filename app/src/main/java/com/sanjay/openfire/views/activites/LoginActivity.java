package com.sanjay.openfire.views.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.sanjay.openfire.R;
import com.sanjay.openfire.service.XMPP;

public class LoginActivity extends AppCompatActivity {
    EditText e1, e2;
    Button b1, b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        e1 = findViewById(R.id.username);
        e2 = findViewById(R.id.password);
        b1 = findViewById(R.id.email_sign_in_button);
        b2 = findViewById(R.id.email_register_button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = e1.getText().toString();
                String password = e2.getText().toString();
                boolean isLogged=XMPP.getInstance().reconnectAndLogin(username);
                if(isLogged)
                    startActivity(new Intent(LoginActivity.this, ChatActivity.class));
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistationActivity.class));
            }
        });
    }
}
