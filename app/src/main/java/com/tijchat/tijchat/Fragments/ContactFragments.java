package com.tijchat.tijchat.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
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
import com.tijchat.tijchat.View.*;
import com.tijchat.tijchat.module.tomodachi;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragments extends Fragment {

    private RecyclerView mContactList;
    private DatabaseReference mContactReference;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    private  View mMainView;
    String online_user_id;

    public ContactFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
      mMainView = inflater.inflate(R.layout.fragment_contact_fragments, container, false);

        mContactList = (RecyclerView) mMainView.findViewById(R.id.contact_list);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        mContactReference = FirebaseDatabase.getInstance().getReference().child("Tomodachi").child(online_user_id);


        mContactList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<tomodachi, tomodachiViewHolder >firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<tomodachi, tomodachiViewHolder>
                        (
                                tomodachi.class,
                                R.layout.user_display_layout,
                                tomodachiViewHolder.class,
                                mContactReference
                        ) {
                    @Override
                    protected void populateViewHolder(final tomodachiViewHolder viewHolder, tomodachi model, final int position) {

                                viewHolder.setDate(model.getDate());

                        final String list_User_id = getRef(position).getKey();

                        userRef.child(list_User_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {

                                final String userName = dataSnapshot.child("User_Name").getValue().toString();
                                String image = dataSnapshot.child("User_image").getValue().toString();

                                viewHolder.setUserName(userName);
                                viewHolder.setUserImage(image, getContext());


                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        CharSequence options[] = new CharSequence[]
                                                {
                                                        userName + "'s profile", "message"
                                                };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("select");

                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int postion) {

                                                if (postion == 0){
                                                    Intent proifleIntent = new Intent(getContext(), Profile.class);
                                                    proifleIntent.putExtra("visit_id", list_User_id);
                                                    startActivity(proifleIntent);
                                                }
                                                if (postion == 1){
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
                                                                chatIntent.putExtra("visit_id", list_User_id);
                                                                chatIntent.putExtra("User_Name", userName);
                                                                startActivity(chatIntent);

                                                            }
                                                        });
                                                    }
                                                }

                                            }
                                        });
                                        builder.show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                };

                mContactList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class tomodachiViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public tomodachiViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDate(String date){
            TextView sinceTomodachiDate = (TextView) mView.findViewById(R.id.all_users_status);
            sinceTomodachiDate.setText(date);
        }

        public void  setUserName(String userName){

            TextView userNameDispay = (TextView) mView.findViewById(R.id.all_users_username);
            userNameDispay.setText(userName);
        }

        public void setUserImage(String User_image, Context ctx) {

            CircleImageView image = (CircleImageView) mView.findViewById(R.id.all_user_profile_image);

            Picasso.with(ctx).load(User_image).into(image);
        }
    }
}
