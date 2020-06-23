package com.android.taptap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuth extends AppCompatActivity {
    
    String verificationCodeBySystem;
    private EditText phoneNo;
    private EditText otp;
    private Button verify;
    private ProgressBar progressBar;
    String mPhoneNumber;


    private void sendVerificationCodeToUser (String mPhoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mPhoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneAuth.this, "Couldn't send OTP", Toast.LENGTH_SHORT);

        }
    };

    private void verifyCode(String codeByUser) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
            signInTheUserByCredentials(credential);
        }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(PhoneAuth.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Intent intent = new Intent(PhoneAuth.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("phoneNumber", mPhoneNumber);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(PhoneAuth.this, "Unable to verify", Toast.LENGTH_SHORT);
                            }
                        }
            }       );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        phoneNo = findViewById(R.id.phoneNo);
        otp = findViewById(R.id.TextOtp);
        verify = findViewById(R.id.verify);
        progressBar = findViewById(R.id.phoneAuthProgreeBar);

        Utils.darkenStatusBar(this, R.color.black);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPhoneNumber = phoneNo.getText().toString();

                if (TextUtils.isEmpty(mPhoneNumber)){
                    phoneNo.setError("Enter phone number");
                    return;
                } else if(!TextUtils.isDigitsOnly(mPhoneNumber) || mPhoneNumber.length()<10){
                    phoneNo.setError("Invalid phone number");
                    return;
                } else if(TextUtils.isDigitsOnly(mPhoneNumber) && !(TextUtils.isEmpty(mPhoneNumber))){

                    phoneNo.setEnabled(false);
                    otp.setVisibility(View.VISIBLE);
                    verify.setText("Verify OTP");

                    sendVerificationCodeToUser(mPhoneNumber);

                    verify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                             String enteredOtp = otp.getText().toString();

                            if (TextUtils.isEmpty(enteredOtp)){
                                otp.setError("Enter OTP");
                            } else if(!TextUtils.isDigitsOnly(enteredOtp) || enteredOtp.length()<6){
                                otp.setError("Invalid OTP");
                            } else if(TextUtils.isDigitsOnly(enteredOtp) && !(TextUtils.isEmpty(enteredOtp))) {

                                progressBar.setVisibility(View.VISIBLE);
                                verifyCode(enteredOtp);
                                
                            }
                        }
                    });
                }
            }
        });

    }
}