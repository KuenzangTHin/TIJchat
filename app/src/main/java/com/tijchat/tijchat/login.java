package com.tijchat.tijchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private Button SignInButton;
    private TextView createUserAccount;
    private EditText userName, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign In");

        userName = (EditText) findViewById(R.id.Email);
        userPassword = (EditText) findViewById(R.id.password);
        SignInButton = (Button)findViewById(R.id.signIn);
        createUserAccount = (TextView) findViewById(R.id.AccountCreate);

        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = userName.getText().toString();
                String password = userPassword.getText().toString();

                UserLogin(email, password);
            }
        });

        createUserAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(login.this, CreateUserAccount.class);
                startActivity(register);
            }
        });
    }

    private void UserLogin(String email, String password) {

        if (TextUtils.isEmpty(email)){
            Toast.makeText(login.this, "Write your email..", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(login.this, "Need password..", Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        Intent mainIntent = new Intent(login.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();                    }
                    else {
                        Toast.makeText(login.this, "Wrong Info, Check your Info." , Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


}

