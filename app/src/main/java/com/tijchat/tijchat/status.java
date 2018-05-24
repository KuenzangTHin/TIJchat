package com.tijchat.tijchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class status extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button saveStatus;
    private EditText statusInput;
    private ProgressDialog loading;

    private DatabaseReference changeStatus;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        changeStatus = FirebaseDatabase.getInstance().getReference().child("Users").child(
                user_id
        );

        mToolbar = (Toolbar) findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Status");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        saveStatus = (Button) findViewById(R.id.save_status);
        statusInput = (EditText) findViewById(R.id.status_input);
        loading = new ProgressDialog(this);

        String old_status = getIntent().getExtras().get("User_status").toString();
        statusInput.setText(old_status);

        saveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String new_status = statusInput.getText().toString();

                ChangeStatus(new_status);
            }
        });
    }

    private void ChangeStatus(String new_status) {

        if (TextUtils.isEmpty(new_status)){

            Toast.makeText(status.this, "Status field Empty", Toast.LENGTH_SHORT).show();

        }else {

            loading.setTitle("Change status");
            loading.setMessage("changing status, please wait.....");
            loading.show();


            changeStatus.child("User_status").setValue(new_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){

                        loading.dismiss();

                        Intent settingsIntent = new Intent(status.this, settings.class);
                        startActivity(settingsIntent);

                        Toast.makeText(status.this, "Successfully Changed", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Toast.makeText(status.this, "Failed", Toast.LENGTH_SHORT).show();

                    }

                }
            });
        }
    }
}
