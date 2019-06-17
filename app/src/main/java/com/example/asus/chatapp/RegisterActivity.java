package com.example.asus.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "NADIA";
    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateAccBtn;

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDisplayName = findViewById(R.id.reg_display_name);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mCreateAccBtn = findViewById(R.id.reg_create_btn);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mCreateAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayName = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(displayName) || !TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    register_user(displayName, email, password);
                }

            }
        });
    }

//    private void updateUI(FirebaseUser user){
//
//    }

    private void register_user(final String displayName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            /*Fetching userid from Firebase created during authentication*/
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();

                            //mDatabase is pointing out to the root directory
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String, String > userHashMap = new HashMap<>();
                            userHashMap.put("name", displayName);
                            userHashMap.put("status", "Hi there! You are using Bazinga Chat App!");
                            userHashMap.put("image", "default");
                            userHashMap.put("thumb_image", "default");

                            mDatabase.setValue(userHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }
                            });


                        } else {
                            String error = "";
                            // If sign in fails, display a message to the user.
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthWeakPasswordException e) {
                                error = "Weak Password";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                error = "Invalid Email";
                            } catch (FirebaseAuthUserCollisionException e) {
                                error = "Account Already Exists";
                            } catch (Exception e) {
                                error = "Unknown Error";
                                e.printStackTrace();
                            }
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
