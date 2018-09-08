package com.example.alirz.mychatapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Messages> mMessageList;


    public MessageAdapter(List<Messages> messagesList){
        this.mMessageList=messagesList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_messages_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        String current_user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();
        String from_user=mMessageList.get(position).getFrom();
        String message_type =mMessageList.get(position).getType();
        String seen= String.valueOf(mMessageList.get(position).getTime());


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String display_name=dataSnapshot.child("name").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();

                holder.name_text.setText(display_name);
                Picasso.get().load(image).placeholder(R.drawable.u).into(holder.profile_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (from_user.equals(current_user_id)){

            holder.message_text.setBackgroundColor(Color.WHITE);
            holder.message_text.setTextColor(Color.BLUE);

        }else{
            holder.message_text.setBackgroundResource(R.drawable.message_text_background);
            holder.message_text.setTextColor(Color.WHITE);
        }


        if (message_type.equals("text")){

            holder.message_text.setText(mMessageList.get(position).getMessage());

        }else{
            holder.send_image.setVisibility(View.VISIBLE);

            Picasso.get().load(mMessageList.get(position).getMessage()).placeholder(R.drawable.u).into(holder.send_image);
        }

        holder.time_text.setText(seen);




    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView message_text,time_text,name_text;
        private CircleImageView profile_image;
        private ImageView send_image;

        public ViewHolder(View itemView) {
            super(itemView);

            message_text=itemView.findViewById(R.id.single_message);
            profile_image=itemView.findViewById(R.id.single_message_image);
            time_text=itemView.findViewById(R.id.single_time);
            name_text=itemView.findViewById(R.id.single_messages_name);
            send_image=itemView.findViewById(R.id.single_send_image);
        }
    }
}
