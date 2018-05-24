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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateUserAccount extends AppCompatActivity {

    private Toolbar mToolbar;
    private ProgressDialog loadingBar;


    private FirebaseAuth mAuth;
    private DatabaseReference storeUserData;
    private EditText UserRegister, UserPasswordRegister, EmailRegister;
    private Button createAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_account);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.create_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        UserRegister = (EditText) findViewById(R.id.user_name);
        UserPasswordRegister = (EditText) findViewById(R.id.user_password);
        EmailRegister = (EditText) findViewById(R.id.user_Email);
        createAcc = (Button) findViewById(R.id.Create);
        loadingBar = new ProgressDialog(this);


        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = UserRegister.getText().toString();
                String password = UserPasswordRegister.getText().toString();
                String email = EmailRegister.getText().toString();

                RegisterAccount (name, password, email);
            }
        });
    }

    private void RegisterAccount(final String name, String password, String email) {

        if (TextUtils.isEmpty(name)){
            Toast.makeText(CreateUserAccount.this, "please write your name." , Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(CreateUserAccount.this, "please write your email." , Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(email)){
            Toast.makeText(CreateUserAccount.this, "please write password." , Toast.LENGTH_LONG).show();
        }

        else {

            loadingBar.setTitle("Signing up new Account");
            loadingBar.setMessage("please wait while we set your account");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        String current_user_Id = mAuth.getCurrentUser().getUid();
                       storeUserData = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_Id);

                        storeUserData.child("User_Name").setValue(name);
                        storeUserData.child("User_status").setValue("Ohayo i am sleepy");
                        storeUserData.child("User_image").setValue("profiledef");
                        storeUserData.child("User_thumb_image").setValue("imagedef")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful())
                                        {
                                            Intent mainIntent = new Intent(CreateUserAccount.this, MainActivity.class);
                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(mainIntent);
                                            finish();
                                        }
                                    }
                                });
                    }

                    else
                        {
                        Toast.makeText(CreateUserAccount.this, "Failed,Try Again.. :)", Toast.LENGTH_SHORT).show();
                    }

                loadingBar.dismiss();
                }
            });
        }
    }
}
