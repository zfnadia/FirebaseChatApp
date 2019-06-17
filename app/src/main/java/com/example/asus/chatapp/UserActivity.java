package com.example.asus.chatapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    DatabaseReference reference;
    private Toolbar mUserToolbar;
    RecyclerView recyclerView;
    ArrayList<User> list;
    RecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mUserToolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(mUserToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.all_user_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //show default image if user sets no image in  the recycleview
                String defaultImgPath = String.valueOf(Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID +  "/drawable/default_avatar"));
                list = new ArrayList<User>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    User user = dataSnapshot1.getValue(User.class);
                    assert user != null;
                    user.key = dataSnapshot1.getKey();
                    if (user.image.equals("default")) {
                        user.image = defaultImgPath;
                    }
                    list.add(user);
                }
                adapter = new RecyclerViewAdapter(UserActivity.this, list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserActivity.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
