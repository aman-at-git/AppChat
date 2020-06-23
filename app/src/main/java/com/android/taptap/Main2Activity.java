package com.android.taptap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class Main2Activity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button login;
    private FirebaseAuth mAuth;
    private TextView create_acc;
    private EditText mName;
    private ProgressBar progress;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.btn_login);
        create_acc = findViewById(R.id.alredy_acc);
        mName = findViewById(R.id.name);
        mAuth = FirebaseAuth.getInstance();
        progress = findViewById(R.id.progressBar2);

        Utils.darkenStatusBar(this, R.color.colorPrimaryDark);

        final String mPhoneNumber = getIntent().getStringExtra("phoneNumber");

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        final Animation fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = username.getText().toString().trim();
                final String pass = password.getText().toString().trim();
                final String name = mName.getText().toString().trim();

                if(TextUtils.isEmpty(userName)){
                    username.setError("Email can't be left blank");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    password.setError("Password can't be left blank");
                    return;
                }
                if(pass.length()<6){
                    password.setError("Password too short");
                    return;
                }
                if(TextUtils.isEmpty(name)){
                    mName.setError("Name can't be left blank");
                    return;
                }
                if(!TextUtils.isEmpty(userName) || !TextUtils.isEmpty(pass) || !TextUtils.isEmpty(name)){

                    progress.setVisibility(View.VISIBLE);
                    progress.setAnimation(fade_in);

                    mAuth.createUserWithEmailAndPassword(userName, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()) {

                                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = current_user.getUid();
                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("phoneNumber", mPhoneNumber);
                                userMap.put("name", name);
                                userMap.put("status", "Hi there! I'm using ChatApp.");
                                userMap.put("image", "default");
                                userMap.put("thumb_image", "default");
                                userMap.put("pass",pass);
                                mDatabase.setValue(userMap);

                                mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                                @Override
                                                public void onSuccess(InstanceIdResult instanceIdResult) {

                                                    String device_token = instanceIdResult.getToken();

                                                    mUserDatabase.child(mAuth.getCurrentUser().getUid()).child("device_token").setValue(device_token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            Toast.makeText(Main2Activity.this, "Authentication Success.", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(getApplicationContext(), Main3Activity.class));
                                                            finish();

                                                        }
                                                    });
                                                }
                                            });

                                        }
                                    }
                                });
                            }

                            else{
                                progress.setVisibility(View.INVISIBLE);
                                Toast.makeText(Main2Activity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }}
        });

        create_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
}