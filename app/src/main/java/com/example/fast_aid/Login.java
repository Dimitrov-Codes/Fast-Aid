package com.example.fast_aid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView myTextView = findViewById(R.id.TextViewPrompt), LoginError = findViewById(R.id.LoginError);
        EditText etPhNumLogin = findViewById(R.id.etPhoneLogin);
        Button btnLogin = findViewById(R.id.btnLogin);
        findViewById(R.id.btnLogin).setOnClickListener(view -> {
            if (!etPhNumLogin.getText().toString().isEmpty() && etPhNumLogin.getText().length() == 10) {
                Intent intent = new Intent(Login.this, OtpSection.class);
                intent.putExtra("Phone Number", "+91" + etPhNumLogin.getText().toString());
                intent.putExtra("Register", false);
                startActivity(intent);
            } else {

                LoginError.setText("Please enter a valid Phone Number");
                LoginError.setVisibility(View.VISIBLE);
            }
        });


    }
}