package com.android.taptap;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okio.Buffer;

public class TapTap extends Application {

  private DatabaseReference mUserDatabase;
  private FirebaseAuth mAuth;

  @Override
  public void onCreate() {
    super.onCreate();

    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    ///----------- PICASSO----------------

    Picasso.Builder builder = new Picasso.Builder(this);
    builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
    Picasso built = builder.build();
    built.setIndicatorsEnabled(true);
    built.setLoggingEnabled(true);
    Picasso.setSingletonInstance(built);

    mAuth = FirebaseAuth.getInstance();
    FirebaseUser current_user = mAuth.getCurrentUser();
    if(current_user != null) {
      mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users")
              .child(mAuth.getCurrentUser().getUid());

      mUserDatabase.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

          if (dataSnapshot != null) {

            mUserDatabase.child("online").onDisconnect().setValue(false);
            mUserDatabase.child("lastSeen").onDisconnect().setValue(ServerValue.TIMESTAMP);

          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });
    }

  }
}
