package com.tijchat.tijchat.View;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.tijchat.tijchat.R;
import com.tijchat.tijchat.module.Users;

import de.hdodenhof.circleimageview.CircleImageView;

public class searchUser extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView sUserList;
    private DatabaseReference sDataUserReference;
    private EditText searchText;
    private ImageButton searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        mToolbar = (Toolbar) findViewById(R.id.search_user_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        searchButton = (ImageButton) findViewById(R.id.search_user_button);
        searchText = (EditText) findViewById(R.id.search_input_text);

        sUserList = (RecyclerView) findViewById(R.id.user_search_list);
        sUserList.setHasFixedSize(true);
        sUserList.setLayoutManager(new LinearLayoutManager(this));


        sDataUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        sDataUserReference.keepSynced(true);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String searchUserName = searchText.getText().toString();

                if (TextUtils.isEmpty(searchUserName)){

                    Toast.makeText(searchUser.this,"please write User Name",Toast.LENGTH_SHORT).show();
                }

                searchForUsers(searchUserName);
            }
        });
    }
    private void searchForUsers(String searchUserName)
     {
         Query searchUserandFriends = sDataUserReference.orderByChild("User_Name")
                 .startAt(searchUserName).endAt(searchUserName + "\uff8f");


        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, UsersViewHolder>
                        (
                                Users.class,
                                R.layout.user_display_layout,
                                UsersViewHolder.class,
                                searchUserandFriends
                                )
                {
                    @Override
                    protected void populateViewHolder(UsersViewHolder viewHolder, Users model, final int position) {


                        viewHolder.setUser_Name(model.getUser_Name());
                        viewHolder.setUser_status(model.getUser_status());
                        viewHolder.setUser_image(getApplicationContext(),model.getUser_image());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String visit_id = getRef(position).getKey();

                                Intent profileIntent = new Intent(searchUser.this, Profile.class);
                                profileIntent.putExtra("visit_id", visit_id);
                                startActivity(profileIntent);
                            }
                        });
                    }
                };

                sUserList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setUser_Name(String User_Name){

            TextView name = (TextView) mView.findViewById(R.id.all_users_username);
            name.setText(User_Name);
        }
        public void setUser_status(String User_status){
            TextView status = (TextView) mView.findViewById(R.id.all_users_status);
            status.setText(User_status);
        }
        public void setUser_image(Context ctx,String User_image){
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.all_user_profile_image);

            Picasso.with(ctx).load(User_image).into(image);
        }
    }

}


