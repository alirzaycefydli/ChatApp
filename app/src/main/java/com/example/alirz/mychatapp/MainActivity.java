package com.example.alirz.mychatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    //wids
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;


    //vars
    private FirebaseAuth mAuth;
    private SectionsPagerAdapter mAdapter;
    private DatabaseReference userRef;

    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        mToolbar=findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        mViewPager=findViewById(R.id.main_container);
        mAdapter= new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mTabLayout=findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        current_user=mAuth.getCurrentUser();

        userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

    }

    @Override
    protected void onStart() {
        super.onStart();



        if (current_user == null){
            startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
            finish();
        }else{
            userRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (current_user != null) {

            userRef.child("online").setValue(false);
            userRef.child("lastseen").setValue(ServerValue.TIMESTAMP);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

     getMenuInflater().inflate(R.menu.main_manu,menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                Intent i =new Intent(MainActivity.this,WelcomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                break;

            case R.id.settings:
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));

                break;

            case R.id.users:
                startActivity(new Intent(MainActivity.this,UsersActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
