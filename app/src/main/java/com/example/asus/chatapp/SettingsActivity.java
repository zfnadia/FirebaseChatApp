package com.example.asus.chatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private CircleImageView mSettingsDisplayImage;
    private TextView mSettingsDisplayName;
    private TextView mSettingStatus;

    private Button mChangeImage;
    private Button mChangeStatus;

    //    private static final int GALLARY_PICK = 1;
    //Storage reference
    private StorageReference mStorageRef;
    private String currentUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSettingsDisplayImage = findViewById(R.id.settings_image);
        mSettingsDisplayName = findViewById(R.id.settings_display_name);
        mSettingStatus = findViewById(R.id.settings_status);

        mChangeImage = findViewById(R.id.settings_img_btn);
        mChangeStatus = findViewById(R.id.settings_status_btn);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, StatusActivity.class));
            }
        });

        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
            }
        });

        //Fetching data from Firebase database
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String thumbImage = dataSnapshot.child("thumb_image").getValue().toString();

                final long initTime = System.currentTimeMillis();
                mSettingsDisplayName.setText(name);
                mSettingStatus.setText(status);
                //if profile image isn't uploaded; set it to default image
                if (!image.equals("default")) {
                    Picasso.get().load(thumbImage).placeholder(R.drawable.default_avatar).into(mSettingsDisplayImage);
                } else {
                    Picasso.get().load(R.drawable.default_avatar).into(mSettingsDisplayImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File thumbBitmapFile = new File(resultUri.getPath());
                Bitmap compressedImageBitmap = null;
                try {
                    compressedImageBitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(70)
                            .compressToBitmap(thumbBitmapFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumbImgByte = baos.toByteArray();

                StorageReference filepath = mStorageRef.child("profile_images").child(currentUid + ".jpg");
                final StorageReference thumbImagePath = mStorageRef.child("profile_images").child("thumbs").child(currentUid + ".jpg");

                filepath.putFile(result.getUri()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mUserDatabase.child("image").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                }
                            });

                            UploadTask uploadTask = thumbImagePath.putBytes(thumbImgByte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    thumb_task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            mUserDatabase.child("thumb_image").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Log.e("Thumb Image", "Upload complete");
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(SettingsActivity.this, "Uri not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
    }
}
