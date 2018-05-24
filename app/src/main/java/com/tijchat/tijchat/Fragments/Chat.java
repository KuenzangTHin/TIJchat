package com.tijchat.tijchat.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tijchat.tijchat.R;
import com.tijchat.tijchat.View.CHat;
import com.tijchat.tijchat.View.Profile;
import com.tijchat.tijchat.module.Chats;
import com.tijchat.tijchat.module.tomodachi;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {

    private View mMainView;

    private RecyclerView mChatList;

    private DatabaseReference mContactReference;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    String online_user_id;


    public Chat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mMainView = inflater.inflate(R.layout.fragment_chat, container, false);

        mChatList = (RecyclerView) mMainView.findViewById(R.id.chat_list);


        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        mContactReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mChatList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mChatList.setLayoutManager(linearLayoutManager);



        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {

        super.onStart();

        FirebaseRecyclerAdapter<Chats, Chat.ChatViewHolder>firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Chats, ChatViewHolder>
                        (
                                Chats.class,
                                R.layout.user_display_layout,
                                Chat.ChatViewHolder.class,
                                mContactReference
                        ) {
                    @Override
                    protected void populateViewHolder(final Chat.ChatViewHolder viewHolder, Chats model, final int position) {

                        final String list_User_id = getRef(position).getKey();

                        userRef.child(list_User_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {

                                final String userName = dataSnapshot.child("User_Name").getValue().toString();
                                String image = dataSnapshot.child("User_image").getValue().toString();

                                String userStatus = dataSnapshot.child("User_status").getValue().toString();

                                viewHolder.setUserName(userName);
                                viewHolder.setUserImage(image, getContext());

                                viewHolder.setUserStatus(userStatus);

                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if (dataSnapshot.child("online").exists()){
                                            Intent chatIntent = new Intent(getContext(), CHat.class);
                                            chatIntent.putExtra("visit_id", list_User_id);
                                            chatIntent.putExtra("User_Name", userName);
                                            startActivity(chatIntent);
                                        }
                                        else {
                                            userRef.child(list_User_id).child("online")
                                                    .setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Intent chatIntent = new Intent(getContext(), CHat.class);
                                                    chatIntent.putExtra("visit_id",list_User_id);
                                                    chatIntent.putExtra("User_Name", userName);
                                                    startActivity(chatIntent);

                                                }
                                            });
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                };

        mChatList.setAdapter(firebaseRecyclerAdapter);

    }




    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ChatViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void  setUserName(String userName){

            TextView userNameDispay = (TextView) mView.findViewById(R.id.all_users_username);
            userNameDispay.setText(userName);
        }

        public void setUserImage(String User_image, Context ctx) {

            CircleImageView image = (CircleImageView) mView.findViewById(R.id.all_user_profile_image);

            Picasso.with(ctx).load(User_image).into(image);
        }

        public void setUserStatus(String userStatus) {

        TextView user_status = (TextView) mView.findViewById(R.id.all_users_status);

            user_status.setText(userStatus);
        }
    }
}
