package com.tijchat.tijchat.Fragments;


import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.tijchat.tijchat.module.AddRequest;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Add extends Fragment {

    private Button addRequestBtn;
    private Button cancelRequestBtn;

    private RecyclerView mAddList;
    private View mMainView;

    private DatabaseReference addRequestReference;
    private DatabaseReference friendsDatabaseRef;
    private DatabaseReference friendsReqDatabaseRef;

    private FirebaseAuth mAuth;
    String online_user_id;
    private DatabaseReference usersReference;

    public Add() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_add, container, false);

        addRequestBtn = (Button) mMainView.findViewById(R.id.request_add_button);
        cancelRequestBtn = (Button) mMainView.findViewById(R.id.request_cancel_btn);

        mAddList = (RecyclerView) mMainView.findViewById(R.id.request_list);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        addRequestReference = FirebaseDatabase.getInstance().getReference().child("Request_to_Add").child(online_user_id);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        friendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        friendsReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Request_to_Add");

        mAddList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mAddList.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment
        return mMainView;
    }



    @Override
    public void onStart() {
        super.onStart();

    FirebaseRecyclerAdapter<AddRequest, Add.AddViewHolder> firebaseRecyclerAdapter =
            new FirebaseRecyclerAdapter<AddRequest, AddViewHolder>
                    (
                            AddRequest.class,
                            R.layout.tomodachi_request_users_layout,
                            Add.AddViewHolder.class,
                            addRequestReference
                    )
            {
                @Override
                protected void populateViewHolder(final AddViewHolder viewHolder, AddRequest model, int position) {

                    final String list_users_id = getRef(position).getKey();

                    DatabaseReference get_type_ref = getRef(position).child("request_Type").getRef();

                    get_type_ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()){

                                String request_type = dataSnapshot.getValue().toString();

                                if (request_type.equals("received")){

                                    usersReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            final String userName = dataSnapshot.child("User_Name").getValue().toString();
                                            final String image = dataSnapshot.child("User_image").getValue().toString();

                                            final String userStatus = dataSnapshot.child("User_status").getValue().toString();

                                            viewHolder.setUserName(userName);
                                            viewHolder.setUserImage(image, getContext());
                                            viewHolder.setUserStatus(userStatus);

                                            addRequestBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    Calendar calDate = Calendar.getInstance();
                                                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                    final String saveCurrentDate = currentDate.format(calDate.getTime());

                                                    friendsDatabaseRef.child(online_user_id).child(list_users_id).child("date").setValue(saveCurrentDate)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    friendsDatabaseRef.child(list_users_id).child(online_user_id).child("data").setValue(saveCurrentDate)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                    friendsReqDatabaseRef.child(online_user_id).child(list_users_id).removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                                    if (task.isSuccessful()){

                                                                                                        friendsReqDatabaseRef.child(list_users_id).child(online_user_id).removeValue()
                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                                        if (task.isSuccessful()){
                                                                                                                            Toast.makeText(getContext(),"Added Successfully",Toast.LENGTH_LONG).show();
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
                                            });

                                            cancelRequestBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    friendsReqDatabaseRef.child(online_user_id).child(list_users_id).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()){

                                                                        friendsReqDatabaseRef.child(list_users_id).child(online_user_id).removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if (task.isSuccessful()){
                                                                                           Toast.makeText(getContext(),"Cancelled successfully!",Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });

                                                                    }

                                                                }
                                                            });

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                                else if (request_type.equals("sender")){

                                    usersReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            final String userName = dataSnapshot.child("User_Name").getValue().toString();
                                            final String image = dataSnapshot.child("User_image").getValue().toString();

                                            final String userStatus = dataSnapshot.child("User_status").getValue().toString();

                                            viewHolder.setUserName(userName);
                                            viewHolder.setUserImage(image, getContext());
                                            viewHolder.setUserStatus(userStatus);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            };
        mAddList.setAdapter(firebaseRecyclerAdapter);
    }




    public static class AddViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public AddViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setUserName(String userName) {

            TextView userNameDisplay = (TextView) mView.findViewById(R.id.request_profile_name);
            userNameDisplay.setText(userName);
        }

        public void setUserImage(final String User_image, final Context ctx) {

            CircleImageView image = (CircleImageView) mView.findViewById(R.id.all_user_profile_image);

            Picasso.with(ctx).load(User_image).into(image);
        }

        public void setUserStatus(String userStatus) {

            TextView status = (TextView) mView.findViewById(R.id.request_status);

            status.setText(userStatus);
        }
    }
}
