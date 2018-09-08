package com.example.alirz.mychatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser();
    }

    public void loginButtonClk(View view) {

        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
    }

    public void createAccountClk(View view) {

        startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (current_user != null) {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }


    }
}
