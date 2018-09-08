package com.example.alirz.mychatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView mListView;
    private EditText mSearchParams;

    private DatabaseReference mRef;
    private List<User> mUsersList;

    private UserAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        mRef = FirebaseDatabase.getInstance().getReference().child("Users");


        mToolbar = findViewById(R.id.users_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = findViewById(R.id.users_list);
        mSearchParams=findViewById(R.id.search_bar);

        closeKeyboard();
        textListener();

    }

    private void textListener(){
        mUsersList=new ArrayList<>();
        mSearchParams.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text=mSearchParams.getText().toString().toLowerCase(Locale.getDefault());
                searchForMatch(text);
            }
        });
    }

    private void searchForMatch(String input){

        mUsersList.clear();

        if (input.length() != 0){

            DatabaseReference mRef=FirebaseDatabase.getInstance().getReference();
            Query query=mRef.child("Users").orderByChild("name").equalTo(input);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    for (DataSnapshot singleSnapShot : snapshot.getChildren()){

                        mUsersList.add(singleSnapShot.getValue(User.class));
                        updateUsersList();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                    Toast.makeText(UsersActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void updateUsersList() {

        mAdapter=new UserAdapter(UsersActivity.this,R.layout.single_user_layout,mUsersList);

        mListView.setAdapter(mAdapter);

    }

    private void closeKeyboard(){
        if (getCurrentFocus() != null){
            InputMethodManager imm= (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

}
