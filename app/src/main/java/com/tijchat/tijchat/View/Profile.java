package com.tijchat.tijchat.View;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tijchat.tijchat.R;

public class Profile extends AppCompatActivity {

    private Button requestButton;
    private Button DeclineButton;
    private TextView userName;
    private TextView status;
    private ImageView userImage;

    private DatabaseReference userReference;

    private String CURRENT_STATE;
    private DatabaseReference requestReference;
    private FirebaseAuth mAuth;
    String sender_id;
    String receive_id;

    private DatabaseReference friendReference;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        requestReference = FirebaseDatabase.getInstance().getReference().child("Request_to_Add");
        mAuth = FirebaseAuth.getInstance();
        sender_id = mAuth.getCurrentUser().getUid();

        friendReference = FirebaseDatabase.getInstance().getReference().child("Friends");

        userReference = FirebaseDatabase.getInstance().getReference().child("Users");


        receive_id = getIntent().getExtras().get("visit_id").toString();

        requestButton = (Button) findViewById(R.id.profile_userRequest);
        DeclineButton = (Button) findViewById(R.id.profile_userDecline);
        userName = (TextView) findViewById(R.id.profile_userName);
        status = (TextView) findViewById(R.id.profile_userStatus);
        userImage = (ImageView) findViewById(R.id.profile_userImage);

        CURRENT_STATE = "Not_Tomodachi";


        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("User_Name").getValue().toString();
                String userStatus = dataSnapshot.child("User_status").getValue().toString();
                String image = dataSnapshot.child("User_image").getValue().toString();


                userName.setText(name);
                status.setText(userStatus);
                Picasso.with(Profile.this).load(image).placeholder(R.drawable.profiledef).into(userImage);

                requestReference.child(sender_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                    if (dataSnapshot.hasChild(receive_id)){
                                        String req_type = dataSnapshot.child(receive_id).child("request_Type").getValue().toString();

                                        if (req_type.equals("sender")){
                                            CURRENT_STATE = "request_sent";
                                            requestButton.setText("Cancel request");

                                            DeclineButton.setVisibility(View.INVISIBLE);
                                            DeclineButton.setEnabled(false);
                                        }

                                        else if (req_type.equals("received")){
                                            CURRENT_STATE = "requested_received";
                                            requestButton.setText("Accept request");

                                            DeclineButton.setVisibility(View.VISIBLE);
                                            DeclineButton.setEnabled(true);

                                            DeclineButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    declineFriendRequest();

                                                }
                                            });
                                        }
                                    }


                                else {
                                    requestReference.child(sender_id)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.hasChild(receive_id)){

                                                        CURRENT_STATE = "Tomodachi";
                                                        requestButton.setText("UnTomodahi");

                                                        DeclineButton.setVisibility(View.INVISIBLE);
                                                        DeclineButton.setEnabled(false);
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {


                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DeclineButton.setVisibility(View.INVISIBLE);
        DeclineButton.setEnabled(false);


     if (!sender_id.equals(receive_id)){

         requestButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 requestButton.setEnabled(false);

                 if (CURRENT_STATE.equals("Not_Tomodachi")){

                     sendRequest();

                 }
                 if (CURRENT_STATE.equals("request_sent")){
                     CancelRequest();
                 }

                 if (CURRENT_STATE.equals("request_received")){

                     AcceptRequest();
                 }
                 if (CURRENT_STATE.equals("Tomodachi")){

                     UnfriendaTomodachi();
                 }
             }
         });
     }
     else {

         DeclineButton.setVisibility(View.INVISIBLE);
         requestButton.setVisibility(View.INVISIBLE);
     }
    }

    private void declineFriendRequest() {

        requestReference.child(sender_id).child(receive_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            requestReference.child(receive_id).child(sender_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                requestButton.setEnabled(true);
                                                CURRENT_STATE = "Not_Tomodachi";
                                                requestButton.setText("Send request");

                                                DeclineButton.setVisibility(View.INVISIBLE);
                                                DeclineButton.setEnabled(false);
                                            }
                                        }
                                    });

                        }

                    }
                });

    }





    private void UnfriendaTomodachi() {

        friendReference.child(sender_id).child(receive_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                 requestReference.child(receive_id).child(sender_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {

                         if (task.isSuccessful()){

                             requestButton.setEnabled(true);
                             CURRENT_STATE = "Not_Tomodachi";
                             requestButton.setText("send request");

                             DeclineButton.setVisibility(View.INVISIBLE);
                             DeclineButton.setEnabled(false);
                         }
                     }
                 });

                }
            }
        });

    }




    private void AcceptRequest() {

        Calendar calDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calDate.getTime());

        requestReference.child(sender_id).child(receive_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        requestReference.child(receive_id).child(sender_id).child("data").setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        requestReference.child(sender_id).child(receive_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()){

                                                            requestReference.child(receive_id).child(sender_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (task.isSuccessful()){
                                                                                requestButton.setEnabled(true);
                                                                                CURRENT_STATE = "Tomodachi";
                                                                                requestButton.setText("UnTomodachi");

                                                                                DeclineButton.setVisibility(View.INVISIBLE);
                                                                                DeclineButton.setEnabled(false);
                                                                            }
                                                                        }
                                                                    });

                                                        }

                                                    }
                                                });

                                    }
                                });
                    }
                });

                    }

    private void CancelRequest() {


        requestReference.child(sender_id).child(receive_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            requestReference.child(receive_id).child(sender_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                requestButton.setEnabled(true);
                                                CURRENT_STATE = "Not_Tomodachi";
                                                requestButton.setText("Send request");

                                                DeclineButton.setVisibility(View.INVISIBLE);
                                                DeclineButton.setEnabled(false);
                                            }
                                        }
                                    });

                        }

                    }
                });

    }

    private void sendRequest() {

        requestReference.child(sender_id).child(receive_id).child("request_Type").setValue("Sender")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    requestReference.child(receive_id).child(sender_id).child("request_Type").setValue("Received")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        requestButton.setEnabled(true);
                                        CURRENT_STATE = "request_sent";
                                        requestButton.setText("Cancel request");

                                        DeclineButton.setVisibility(View.INVISIBLE);
                                        DeclineButton.setEnabled(false);
                                    }

                                }
                            });
                }

            }
        });

    }
}
