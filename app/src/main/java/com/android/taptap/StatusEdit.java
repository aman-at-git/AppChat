package com.android.taptap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusEdit extends AppCompatActivity {

  private Toolbar mToolbar;

  private TextInputLayout mstatus;
  private Button mbutton;

  private DatabaseReference mStatusDatabase;
  private FirebaseUser mCurrentUser;
  private ProgressBar progress;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_status_edit);
    mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    String uid = mCurrentUser.getUid();

    mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);


    mToolbar = findViewById(R.id.main_toolbar);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setTitle("Change Status");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    String status_value = getIntent().getStringExtra("status_value");

    mbutton = findViewById(R.id.status_save_btn);
    mstatus = findViewById(R.id.status_input);
    progress = findViewById(R.id.status_progressBar);

    mstatus.getEditText().setText(status_value);

    mbutton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        progress.setVisibility(View.VISIBLE);

        String status = mstatus.getEditText().getText().toString();
        mStatusDatabase.child("status").setValue(status);

        mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){

              startActivity(new Intent(getApplicationContext(), SettingActivity.class));
              finish();;
            }
          }
        });


      }
    });
  }
}
