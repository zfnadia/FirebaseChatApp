package com.example.asus.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    //private DatabaseReference mChatDatabaseRef;
    //private FirebaseUser mCurrentUser;

    User mCurrentUser;
    private FirebaseRecyclerAdapter<ChatMessage, ChatHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Bazinga Chat");

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser frUser = FirebaseAuth.getInstance().getCurrentUser();
        if (frUser == null){
            return;
        }
        String uid = frUser.getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCurrentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final TextView typingView = findViewById(R.id.typing_indicator);

        final DatabaseReference typingRef = FirebaseDatabase.getInstance().getReference().child("Typing");

        typingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String typing = (String) dataSnapshot.getValue();
                if (typing != null && typing.length() > 0 && mCurrentUser != null && !typing.equals(mCurrentUser.name)){
                    typingView.setText(typing + " is typing...");
                    typingView.setVisibility(View.VISIBLE);
                } else {
                    typingView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final EditText inputMessage = findViewById(R.id.input);

        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mCurrentUser != null){
                    typingRef.setValue(mCurrentUser.name);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final DatabaseReference query = FirebaseDatabase.getInstance().getReference().child("Messages");

        FirebaseRecyclerOptions<ChatMessage> options =
                new FirebaseRecyclerOptions.Builder<ChatMessage>()
                        .setQuery(query, ChatMessage.class)
                        .build();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = inputMessage.getText().toString();

                if (mCurrentUser == null){
                    Toast.makeText(MainActivity.this, "User not ready yet", Toast.LENGTH_LONG).show();
                    return;
                }
                Date today = new Date();
                SimpleDateFormat format;
                format = new SimpleDateFormat("hh:mm a");
                String dateToStr = format.format(today);

                ChatMessage chat = new ChatMessage(mCurrentUser.name, message, dateToStr, mCurrentUser.thumb_image);

                String newKey = query.push().getKey();
                query.child(newKey).setValue(chat);
                inputMessage.setText("");
                typingRef.setValue("");
            }
        });

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatHolder>(options) {
            @Override
            public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_message, parent, false);

                return new ChatHolder(view);
            }

            @Override
            protected void onBindViewHolder(ChatHolder holder, int position, ChatMessage model) {
                // Bind the Chat object to the ChatHolder
                // ...
                holder.name.setText(model.getUserName());
                holder.message.setText(model.getMessage());
                holder.deliveryTime.setText(model.getDeliveryTime());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.default_avatar).into(holder.profilePic);
            }
        };

        RecyclerView messagesList = findViewById(R.id.list_of_messages);
        messagesList.setLayoutManager(new LinearLayoutManager(this));
        messagesList.setAdapter(mFirebaseAdapter);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mFirebaseAdapter != null)
            mFirebaseAdapter.stopListening();
    }

    static class ChatHolder extends RecyclerView.ViewHolder {
        TextView name, message, deliveryTime;
        ImageView profilePic;
        //LinearLayout parentLayout;

        public ChatHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.single_msg_name);
            message = itemView.findViewById(R.id.single_message_text);
            deliveryTime = itemView.findViewById(R.id.message_time);
            profilePic = itemView.findViewById(R.id.single_msg_img);
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendToStartActivity();
        }
        if (mFirebaseAdapter != null)
            mFirebaseAdapter.startListening();
    }

    private void sendToStartActivity() {
        startActivity(new Intent(MainActivity.this, StartActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_btn) {
            FirebaseAuth.getInstance().signOut();
            sendToStartActivity();
        }

        if (item.getItemId() == R.id.account_settings_btn) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        if (item.getItemId() == R.id.all_user_btn)
            startActivity(new Intent(MainActivity.this, UserActivity.class));

        return true;
    }

    @Override
    public void onBackPressed() {
        //On back pressed while logged in go back to Home page
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
