package com.example.fast_aid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpSection extends AppCompatActivity {
    static boolean flag = false;
    Button btnSubmit;
    EditText etOtp;
    String verificationId;
    FirebaseAuth mAuth;
    String TAG = "OtpSection";
    String Name, Phone_Number;
    private boolean gps_enabled = false;
    private LocationManager lm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_section);
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        Phone_Number = getIntent().getStringExtra("Phone Number");
        Name = getIntent().getStringExtra("Name");
        btnSubmit = findViewById(R.id.btnSubmit);
        etOtp = findViewById(R.id.etOTP);
        sendVerificationCode(Phone_Number);
        btnSubmit.setOnClickListener((v) -> {
            if (flag) {
                Toast.makeText(this, "You have been temporarily banned from logging in ", Toast.LENGTH_LONG).show();

            } else if (!etOtp.getText().toString().isEmpty()) {
                findViewById(R.id.pbOTP).setVisibility(View.VISIBLE);
                verifyOTP(etOtp.getText().toString());
            } else {
                etOtp.setError("Please enter a valid OTP");
                etOtp.requestFocus();
            }
        });

    }

    private void sendVerificationCode(String PhoneNumber) {

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @org.jetbrains.annotations.NotNull PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                Log.d(TAG, code);
                if (code != null) {
                    etOtp.setText(code);
                    verifyOTP(code);
                }
            }


            @Override
            public void onVerificationFailed(@NonNull @org.jetbrains.annotations.NotNull FirebaseException e) {
                Toast.makeText(OtpSection.this, e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("OtpSection", e.getMessage());
                OtpSection.flag = true;
            }

            @Override
            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verificationId = s;

            }

        };
        PhoneAuthOptions options = PhoneAuthOptions
                .newBuilder(mAuth)
                .setPhoneNumber(PhoneNumber)
                .setActivity(this)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOTP(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        if (gps_enabled) {
                            Intent intent = new Intent(OtpSection.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(OtpSection.this, RequestGPS.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                        }

                    } else {
                        Log.d("OtpSection", Objects.requireNonNull(task.getException()).getMessage());

                        Toast.makeText(OtpSection.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();

                    }

                });

    }
}