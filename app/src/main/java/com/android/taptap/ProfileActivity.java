package com.android.taptap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.android.taptap.R.color.*;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendRequestBtn;
    private Button mFriendRequestDismiss;
    private ProgressBar progress;
    private DatabaseReference mUserDatabase;
    private String mCurrent_state;
    private DatabaseReference mFriendRequestDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notification");
        mAuth = FirebaseAuth.getInstance();

        mFriendRequestDismiss = findViewById(R.id.profile_reject_request_btn);
        progress = findViewById(R.id.progressBar3);
        mProfileImage = findViewById(R.id.profile_image);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileName = findViewById(R.id.profile_display_name);
        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);
        mProfileSendRequestBtn= findViewById(R.id.profile_send_request_btn);

        mCurrent_state = "not friends";

        progress.setVisibility(View.VISIBLE);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                ///------------------  FRIENDS LIST / REQUEST FEATURE

                mFriendRequestDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received")){

                                mCurrent_state = "req_received";
                                mProfileSendRequestBtn.setText("Accept Friend Request Received");
                                mFriendRequestDismiss.setVisibility(View.VISIBLE);

                            }

                            else if(req_type.equals("sent")){

                                mCurrent_state = "req_sent";
                                mProfileSendRequestBtn.setText("Undo Friend Request");
                                mProfileSendRequestBtn.getBackground()
                                        .setColorFilter(ContextCompat.getColor(ProfileActivity
                                                .this, green), PorterDuff.Mode.MULTIPLY);
                            }
                        } else {

                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){

                                        mCurrent_state = "friends";

                                        mProfileSendRequestBtn.getBackground()
                                                .setColorFilter(ContextCompat.getColor(ProfileActivity
                                                        .this, orange), PorterDuff.Mode.MULTIPLY);
                                        mProfileSendRequestBtn.setText("UnFriend" );

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        progress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mFriendRequestDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mProfileSendRequestBtn.setEnabled(true);
                                        mCurrent_state = "not friends";

                                        mProfileSendRequestBtn.getBackground()
                                                .setColorFilter(ContextCompat.getColor(ProfileActivity
                                                        .this, orange), PorterDuff.Mode.MULTIPLY);
                                        mProfileSendRequestBtn.setText("Send Friend Request");
                                    }
                                });

                            }
                        });

                mFriendRequestDismiss.setVisibility(View.GONE);
            }
        });

        mProfileSendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrent_state.equals("not friends")){

                    mProfileSendRequestBtn.setEnabled(false);

                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid())
                                        .child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @SuppressLint("ResourceAsColor")
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                        HashMap<String ,String> notificationData = new HashMap<>();
                                        notificationData.put("from", mCurrentUser.getUid());
                                        notificationData.put("type", "request");

                                        mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                mProfileSendRequestBtn.setEnabled(true);
                                                mCurrent_state = "req_sent";
                                                mProfileSendRequestBtn.getBackground()
                                                        .setColorFilter(ContextCompat.getColor(ProfileActivity
                                                                .this, green), PorterDuff.Mode.MULTIPLY);
                                                mProfileSendRequestBtn.setText("Undo Friend Request");

                                                Toast.makeText(ProfileActivity.this,"Sent", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
                if(mCurrent_state.equals("req_sent")){

                    mProfileSendRequestBtn.setEnabled(false);

                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mProfileSendRequestBtn.setEnabled(true);
                                            mCurrent_state = "not friends";

                                            mProfileSendRequestBtn.getBackground()
                                                    .setColorFilter(ContextCompat.getColor(ProfileActivity
                                                            .this, orange), PorterDuff.Mode.MULTIPLY);
                                            mProfileSendRequestBtn.setText("Send Friend Request");
                                        }
                                    });
                                }
                            });
                }

                if(mCurrent_state.equals("req_received")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());


                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).child("date")
                            .setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendRequestBtn.setEnabled(false);

                                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mProfileSendRequestBtn.setEnabled(true);
                                                    mCurrent_state = "friends";

                                                    mProfileSendRequestBtn.getBackground()
                                                            .setColorFilter(ContextCompat.getColor(ProfileActivity
                                                                    .this, orange), PorterDuff.Mode.MULTIPLY);
                                                    mProfileSendRequestBtn.setText("UnFriend" );

                                                    mFriendRequestDismiss.setVisibility(View.GONE);
                                                }
                                            });

                                        }
                                    });
                                }
                            });
                        }
                    });

                } else if (mCurrent_state.equals("friends")){

                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendRequestBtn.setEnabled(true);
                                    mCurrent_state = "not friends";

                                    mProfileSendRequestBtn.getBackground()
                                            .setColorFilter(ContextCompat.getColor(ProfileActivity
                                                    .this, orange), PorterDuff.Mode.MULTIPLY);
                                    mProfileSendRequestBtn.setText("Send Friend Request");
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.child("online").setValue(true);
    }
}
