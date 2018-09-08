package com.example.alirz.mychatapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends ArrayAdapter<User> {

    private LayoutInflater layoutInflater;
    private List<User> mUsers=null;
    private int layoutResource;
    private Context mContext;

    public UserAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        mContext=context;
        layoutResource=resource;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mUsers=objects;
    }

    private static class ViewHolder {
        TextView user_name, user_status;
        CircleImageView user_image;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        final ViewHolder holder;

        if (convertView == null){
            convertView =layoutInflater.inflate(layoutResource,parent,false);
            holder=new ViewHolder();

            holder.user_name=convertView.findViewById(R.id.single_name);
            holder.user_status=convertView.findViewById(R.id.single_status);
            holder.user_image=convertView.findViewById(R.id.single_image);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }


        holder.user_name.setText(mUsers.get(position).getName());
        holder.user_status.setText(mUsers.get(position).getStatus());

        final String user_id=mUsers.get(position).getUser_id();

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(mContext,ProfileActivity.class);
                intent.putExtra("id",user_id);
                mContext.startActivity(intent);

            }
        });

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query=reference.child("Users").orderByChild("user_id").equalTo(getItem(position).getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 for (final DataSnapshot singleSnpShot : snapshot.getChildren()){
                   Objects.requireNonNull(singleSnpShot.getValue(User.class)).toString();

                     Picasso.get().load(singleSnpShot.getValue(User.class).getImage()).networkPolicy(NetworkPolicy.OFFLINE)
                             .placeholder(R.drawable.u).into(holder.user_image, new Callback() {
                         @Override
                         public void onSuccess() {

                         }

                         @Override
                         public void onError(Exception e) {

                             Picasso.get().load(singleSnpShot.getValue(User.class).getImage()).placeholder(R.drawable.u).into(holder.user_image);
                         }
                     });
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(mContext, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        return convertView;


    }
}
