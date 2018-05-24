package com.tijchat.tijchat.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tijchat.tijchat.MessageAdpater;
import com.tijchat.tijchat.R;
import com.tijchat.tijchat.module.Messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CHat extends AppCompatActivity {

    private String msgReceiverId;
    private String msgReceiverName;
    private Toolbar chatToolbar;

    private TextView userName;
    private TextView userlastOnline;
    private CircleImageView userChatProfileImg;

    private ImageButton sendMessageButton;
    private ImageButton selectImageButton;
    private EditText inputMessageText;

    private DatabaseReference rootRef;
    private StorageReference MessageImageStorageRef;
    private FirebaseAuth mAuth;
    private String messageSenderId;
    private RecyclerView userMessagesList;

    private final List<Messages> messageList = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;
    private MessageAdpater messageAdpater;

    private static int gallaryPic = 1;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();

        msgReceiverId = getIntent().getExtras().get("visit_id").toString();
        msgReceiverName = getIntent().getExtras().get("User_Name").toString();
        MessageImageStorageRef = FirebaseStorage.getInstance().getReference().child("Messages_Images");


        Toast.makeText(CHat.this, msgReceiverId, Toast.LENGTH_SHORT).show();
        Toast.makeText(CHat.this, msgReceiverName, Toast.LENGTH_SHORT).show();

        chatToolbar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolbar);

        loadingBar = new ProgressDialog(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view = layoutInflater.inflate(R.layout.custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        userName = (TextView) findViewById(R.id.custom_profile_name);
        userlastOnline = (TextView) findViewById(R.id.custom_last_seen);
        userChatProfileImg = (CircleImageView) findViewById(R.id.custom_profile_pic);

        sendMessageButton = (ImageButton) findViewById(R.id.send_message);
        selectImageButton = (ImageButton) findViewById(R.id.select_image);
        inputMessageText = (EditText) findViewById(R.id.input_message);

        messageAdpater = new MessageAdpater(messageList);

        userMessagesList = (RecyclerView) findViewById(R.id.message_list);

        linearLayoutManager = new LinearLayoutManager(this);

        userMessagesList.setHasFixedSize(true);

        userMessagesList.setLayoutManager(linearLayoutManager);

        userMessagesList.setAdapter(messageAdpater);

        userMessagesList = (RecyclerView) findViewById(R.id.message_list);


        FetchMessages();



        userName.setText(msgReceiverName);

        rootRef.child("Users").child(msgReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String online = dataSnapshot.child("online").getValue().toString();
                final String userImage = dataSnapshot.child("User_image").getValue().toString();

                Picasso.with(CHat.this).load(userImage).into(userChatProfileImg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               SendMessage();
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, gallaryPic);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallaryPic && resultCode == RESULT_OK && data!=null){

        loadingBar.setTitle("sending image..");
            loadingBar.setMessage("waiting while sending..");
            loadingBar.show();

            Uri imageUri = data.getData();

            final String message_sender_ref = "Messages/" + messageSenderId + "/" + msgReceiverId;
            final String message_receiver_ref = "Messages/" + msgReceiverId + "/" + messageSenderId;

            DatabaseReference user_message_key = rootRef.child("Messages").child(messageSenderId).child(msgReceiverId).push();
            final String message_push_id = user_message_key.getKey();

            StorageReference filePath = MessageImageStorageRef.child(message_push_id + ".jpg");

            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful())
                    {
                        final String downloadUrl = task.getResult().getDownloadUrl().toString();

                        Map messageTextBody = new HashMap();

                        messageTextBody.put("message", downloadUrl);
                        messageTextBody.put("seen", false);
                        messageTextBody.put("type", "image");
                        messageTextBody.put("time", ServerValue.TIMESTAMP);
                        messageTextBody.put("from", messageSenderId);

                        Map messageBody = new HashMap();

                        messageBody.put(message_sender_ref  + "/" + message_push_id, messageTextBody);
                        messageBody.put(message_receiver_ref + "/" + message_push_id, messageTextBody);


                        rootRef.updateChildren(messageTextBody, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError != null){

                                    Log.d("Chat_Log", databaseError.getMessage().toString());
                                }

                                inputMessageText.setText("");
                                loadingBar.dismiss();
                            }
                        });


                        Toast.makeText(CHat.this, "sent...", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else
                        {
                        Toast.makeText(CHat.this, "failed,try again...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void FetchMessages() {

        rootRef.child("Messages").child(messageSenderId).child(msgReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messageList.add(messages);

                        messageAdpater.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void SendMessage() {

        String messageText = inputMessageText.getText().toString();

        if(TextUtils.isEmpty(messageText)){

            Toast.makeText(CHat.this,"Write message..", Toast.LENGTH_SHORT).show();
        }
        else {

            String message_sender_ref = "Messages/" + messageSenderId + "/" + msgReceiverId;

            String message_receiver_ref = "Messages/" + msgReceiverId + "/" + messageSenderId;

            DatabaseReference user_message_key = rootRef.child("Messages").child(messageSenderId).child(msgReceiverId).push();

            String message_push_id = user_message_key.getKey();

            Map messageTextBody = new HashMap();

            messageTextBody.put("message", messageText);
            messageTextBody.put("seen", false);
            messageTextBody.put("type", "text");
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("from", messageSenderId);

            Map messageBody = new HashMap();

            messageBody.put(message_sender_ref + "/" + message_push_id, messageTextBody);
            messageBody.put(message_receiver_ref + "/" + message_push_id, messageTextBody);

            rootRef.updateChildren(messageBody, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){
                        Log.d("chat_log", databaseError.getMessage().toString());
                    }
                    inputMessageText.setText("");
                }
            });
        }
    }
    }

