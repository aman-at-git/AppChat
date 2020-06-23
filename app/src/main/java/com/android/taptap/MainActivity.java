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

public class MainActivity extends AppCompatActivity {



    private EditText username;
    private EditText password;
    private Button login;
    private FirebaseAuth mAuth;
    private TextView create_acc;
    private ProgressBar progress;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //startActivity(new Intent(getApplicationContext(), PhoneAuth.class));
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.phoneNo);
        password = findViewById(R.id.password);
        login = findViewById(R.id.verify);
        create_acc = findViewById(R.id.alredy_acc);
        mAuth = FirebaseAuth.getInstance();
        progress = findViewById(R.id.progressBar);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        Utils.darkenStatusBar(this, R.color.pink);

        final Animation fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            startActivity(new Intent(getApplicationContext(), Main3Activity.class));
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if(!TextUtils.isEmpty(userName) || !TextUtils.isEmpty(pass)){

                    getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                    );
                    progress.setVisibility(View.VISIBLE);
                    progress.setAnimation(fade_in);
                    loginUser(userName, pass);
                }



            }
        });

        create_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), PhoneAuth.class));
                finish();
            }
        });

    }

    private void loginUser(String userName, String pass) {

        mAuth.signInWithEmailAndPassword(userName, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {

                            String device_token = instanceIdResult.getToken();

                            mUserDatabase.child(mAuth.getCurrentUser().getUid()).child("device_token").setValue(device_token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    progress.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(getApplicationContext(), Main3Activity.class));
                                    finish();

                                }
                            });
                        }
                    });



                } else {

                    progress.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
