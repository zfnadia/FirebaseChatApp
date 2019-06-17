package com.example.asus.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mStatusToolbar;
    private TextInputLayout mEditStatus;
    private Button mUpdateStatusBtn;
    private EditText mShowStatus;

    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mStatusToolbar = findViewById(R.id.status_toolbar);
        setSupportActionBar(mStatusToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditStatus = findViewById(R.id.edit_status);
        mUpdateStatusBtn = findViewById(R.id.update_status_btn);

        mUpdateStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = mEditStatus.getEditText().getText().toString();
                mStatusDatabase.child("status").setValue(status);
                startActivity(new Intent(StatusActivity.this, SettingsActivity.class));
            }
        });


    }
}
