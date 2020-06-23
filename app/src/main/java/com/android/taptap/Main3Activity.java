package com.android.taptap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class Main3Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private String title = "WhatsApp";
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mAuth = FirebaseAuth.getInstance();

        //getting the toolbar
        toolbar = findViewById(R.id.main_toolbar);

        //setting the title
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        //COLOR Status Bar
        Utils.darkenStatusBar(this, R.color.purple);

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        //Tabbed Activity

        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){

            Intent loginIntent = new Intent(this, MainActivity.class);
            startActivity(loginIntent);
        }

        else {
            mUserRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUserRef.child("online").setValue(false);
        mUserRef.child("lastSeen").setValue(ServerValue.TIMESTAMP);
    }

    //MENU OPENER
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_button) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        if (item.getItemId() == R.id.main_settings_button){
            startActivity(new Intent(getApplicationContext(), SettingActivity.class));

        }

        if(item.getItemId()== R.id.main_all_user_button){
            Intent all_users_intent= new Intent(Main3Activity.this, UsersActivity.class);
            startActivity(all_users_intent);
        }

        return true;
    }


}