package com.tijchat.tijchat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class settings extends AppCompatActivity {

    private CircleImageView diplayImage;
    private TextView displayName;
    private TextView displayStatus;
    private Button settingChange;
    private final static int gallaryPic = 1;

    private DatabaseReference getUserData;
    private FirebaseAuth mAuth;
    private StorageReference storeImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        String online_user_id = mAuth.getCurrentUser().getUid();
        getUserData = FirebaseDatabase.getInstance().getReference().child("User").child(online_user_id);
        storeImageRef = FirebaseStorage.getInstance().getReference().child("profile_images");


        diplayImage = (CircleImageView) findViewById(R.id.setting_pro_image);
        displayName = (TextView) findViewById(R.id.setting_userName);
        displayStatus = (TextView) findViewById(R.id.setting_status);
        settingChange = (Button) findViewById(R.id.setting_change);


        getUserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("User_Name").getValue().toString();
                String status = dataSnapshot.child("User_status").getValue().toString();
                String image = dataSnapshot.child("User_image").getValue().toString();
                String thumb_image = dataSnapshot.child("User_thumb_image").getValue().toString();

                displayName.setText(name);
                displayStatus.setText(status);

                if (!image.equals("profiledef")){

                    Picasso.with(settings.this).load(image).placeholder(R.drawable.profiledef).into(diplayImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        diplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, gallaryPic);

            }
        });

        settingChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String old_status = displayStatus.getText().toString();

                Intent statusIntent = new Intent(settings.this, status.class);
                statusIntent.putExtra("User_status", old_status);
                startActivity(statusIntent);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallaryPic && resultCode == RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri();

                String User_id = mAuth.getCurrentUser().getUid();
                StorageReference filePath = storeImageRef.child(User_id + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(settings.this,"Saving your Image", Toast.LENGTH_LONG).show();


                           final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            getUserData.child("User_image").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                           Toast.makeText(settings.this, "Uploaded Successfully..", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                        else {
                            Toast.makeText(settings.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }
}
