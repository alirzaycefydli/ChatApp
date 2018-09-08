package com.example.alirz.mychatapp;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyChat extends Application {

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        //Firebase database offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        //Picasso offline //
        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built =builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        mAuth=FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){

            userDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

            userDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null){
                        userDatabase.child("online").onDisconnect().setValue(false);
                      //  DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                      //  String date = df.format(Calendar.getInstance().getTime());


                        DateFormat format =DateFormat.getTimeInstance();
                        String d=format.format(Calendar.getInstance().getTime());
                        userDatabase.child("lastseen").setValue(d);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}
