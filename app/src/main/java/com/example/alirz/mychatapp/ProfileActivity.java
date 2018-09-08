package com.example.alirz.mychatapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    //vidgets
    private TextView profile_name, profile_status, profile_friendsCount;
    private ImageView profile_image;
    private Button send_friend_req, decline_friend_req;


    //vars
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private FirebaseUser firebaseUser;
    private DatabaseReference mFriendRef;
    private DatabaseReference friendDatabase;

    private DatabaseReference notificationDatabase;
    private DatabaseReference rootRef;

    private String current_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        final String user_id = getIntent().getStringExtra("id");
        firebaseUser = mAuth.getCurrentUser();
        final String current_user_id = firebaseUser.getUid();

        rootRef=FirebaseDatabase.getInstance().getReference();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRef = FirebaseDatabase.getInstance().getReference("Friend_req");
        friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        notificationDatabase=FirebaseDatabase.getInstance().getReference().child("notifications");

        profile_name = findViewById(R.id.profile_name);
        profile_friendsCount = findViewById(R.id.profile_friends);
        profile_status = findViewById(R.id.profile_status);
        profile_image = findViewById(R.id.profile_image);

        decline_friend_req = findViewById(R.id.decline_req);
        send_friend_req = findViewById(R.id.send_friend_req);

        current_state = "not_friends";


        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String display_status = dataSnapshot.child("status").getValue().toString();
                String display_image = dataSnapshot.child("image").getValue().toString();


                profile_name.setText(display_name);
                profile_status.setText(display_status);
                Picasso.get().load(display_image).placeholder(R.drawable.u).into(profile_image);


                // Friend list / Request feature/////

                mFriendRef.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild( user_id)) {
                            String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (request_type.equals("received")) {

                                current_state = "received";
                                send_friend_req.setText("Accept frıend request");

                                decline_friend_req.setVisibility(View.VISIBLE);
                                decline_friend_req.setEnabled(true);

                            } else if (request_type.equals("sent")) {

                                current_state = "sent";
                                send_friend_req.setText("Cancel frıend request");
                                decline_friend_req.setVisibility(View.INVISIBLE);
                                decline_friend_req.setEnabled(false);
                            }
                        } else {
                            friendDatabase.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild( user_id)) {
                                        current_state = "friends";
                                        send_friend_req.setText("unfrıend");

                                        decline_friend_req.setVisibility(View.INVISIBLE);
                                        decline_friend_req.setEnabled(false);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    Toast.makeText(ProfileActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Toast.makeText(ProfileActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ProfileActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        send_friend_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                send_friend_req.setEnabled(false);


                // ---------Not friends-------/////////
                if (current_state.equals("not_friends")) {

                    DatabaseReference newNotificationRef=rootRef.child("notifications").child(user_id).push();
                    String newNotificationId=newNotificationRef.getKey();

                    HashMap<String,String> notificationData=new HashMap<>();
                    notificationData.put("from",current_user_id);
                    notificationData.put("type","request");

                    Map<String,Object> requestMap = new HashMap<>();
                    requestMap.put("Friend_req/"+current_user_id+"/"+user_id+"/request_type","sent");
                    requestMap.put("Friend_req/"+user_id+"/"+current_user_id+"/request_type","received");
                    requestMap.put("notifications/"+user_id+"/"+newNotificationId,notificationData);

                    rootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError != null){
                                Toast.makeText(ProfileActivity.this, "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            send_friend_req.setEnabled(true);
                            current_state="sent";
                            send_friend_req.setText("Cancel friend request");
                        }
                    });
                }

                // ---------Cancel friend Request-------/////////

                if (current_state.equals("sent")) {

                    mFriendRef.child( current_user_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                mFriendRef.child(user_id).child(current_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            send_friend_req.setEnabled(true);
                                            current_state = "not_friends";
                                            send_friend_req.setText("Send frıend request");

                                            decline_friend_req.setVisibility(View.INVISIBLE);
                                            decline_friend_req.setEnabled(false);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }


                ///---------------Request Received--------------///

                if (current_state.equals("received")) {

                    final String current_date = DateFormat.getDateInstance().format(new Date());

                    Map<String,Object> friendsMap =new HashMap<>();
                    friendsMap.put("Friends/"+current_user_id+"/"+user_id+"/date",current_date);
                    friendsMap.put("Friends/"+user_id+"/"+current_user_id+"/date",current_date);

                    friendsMap.put("Friend_req/"+current_user_id+"/"+user_id,null);
                    friendsMap.put("Friend_req/"+user_id+"/"+current_user_id,null);

                    rootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError == null){
                                send_friend_req.setEnabled(true);
                                current_state="friends";
                                send_friend_req.setText("Unfrıend");
                                decline_friend_req.setEnabled(false);
                                decline_friend_req.setVisibility(View.INVISIBLE);
                            }else{
                                Toast.makeText(ProfileActivity.this, "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                ///--- Unfriend-----//////
                if (current_state.equals("friends")){

                    Map<String,Object> unfriendMap =new HashMap<>();
                    unfriendMap.put("Friends/"+current_user_id+"/"+user_id,null);
                    unfriendMap.put("Friends/"+user_id+"/"+current_user_id,null);

                    rootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError == null){
                                current_state="not_friends";
                                send_friend_req.setText("Send frıend request");
                                decline_friend_req.setEnabled(false);
                                decline_friend_req.setVisibility(View.INVISIBLE);
                            }else{
                                Toast.makeText(ProfileActivity.this, "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            send_friend_req.setEnabled(true);

                        }
                    });

                }

            }
        });


    }

}
