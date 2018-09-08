package com.example.alirz.mychatapp;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;

    private String current_user_id;
    private DatabaseReference mConvRef;
    private DatabaseReference mMessageRef;
    private DatabaseReference mUserRef;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView=view.findViewById(R.id.chat_frag_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        current_user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mConvRef= FirebaseDatabase.getInstance().getReference().child("Chats").child(current_user_id);

        mUserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        mUserRef.keepSynced(true);
        mMessageRef=FirebaseDatabase.getInstance().getReference().child("messages").child(current_user_id);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery =mConvRef.orderByChild("timestamp");

        FirebaseRecyclerOptions<Conversation> options=
                new FirebaseRecyclerOptions.Builder<Conversation>()
                        .setQuery(conversationQuery,Conversation.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Conversation,ConversationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ConversationViewHolder holder, int position, @NonNull final Conversation model) {

                final String list_user_id =getRef(position).getKey();

                Query lastMessageQuery = mMessageRef.child(list_user_id).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        holder.setMessage(data, model.isSeen());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mUserRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userImage = dataSnapshot.child("image").getValue().toString();

                        holder.userNameView.setText(userName);

                        Picasso.get().load(userImage).placeholder(R.drawable.u).into(holder.userImageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("chat_user_id", list_user_id);
                                chatIntent.putExtra("chat_user_name", userName);
                                startActivity(chatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Toast.makeText(getContext(), "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }


            @NonNull
            @Override
            public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_conversation_layout,parent,false);

                return new ConversationViewHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);




    }


    public static class ConversationViewHolder extends RecyclerView.ViewHolder{

        TextView userStatusView;
        TextView userNameView;
        CircleImageView userImageView;

        public ConversationViewHolder(View itemView) {
            super(itemView);

            userStatusView =  itemView.findViewById(R.id.conv_message);
            userNameView =  itemView.findViewById(R.id.conv_name);
            userImageView = itemView.findViewById(R.id.conv_image);
        }

        public void setMessage(String message, boolean isSeen){

            userStatusView.setText(message);

            if(!isSeen){
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            } else {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            }

        }


    }
}
