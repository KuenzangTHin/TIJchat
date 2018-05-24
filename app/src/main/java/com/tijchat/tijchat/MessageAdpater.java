package com.tijchat.tijchat;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.tijchat.tijchat.module.Messages;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DELL PC on 19/05/2018.
 */

public class MessageAdpater extends RecyclerView.Adapter<MessageAdpater.MessageViewHolder>{

    private List<Messages> userMessagesList;

    private FirebaseAuth mAuth;

    public MessageAdpater(List<Messages> userMessagesList){

        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_list_layout, parent, false);


        mAuth = FirebaseAuth.getInstance();

        return  new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {


        String message_sender_id = mAuth.getCurrentUser().getUid();

        Messages messages = userMessagesList.get(position);

        String fromUserId = messages.getFrom();

        if (fromUserId.equals(message_sender_id)){

            holder.messageText.setBackgroundResource(R.drawable.second_message_list_background);

            holder.messageText.setTextColor(Color.BLACK);

            holder.messageText.setGravity(Gravity.RIGHT);

        }
        else {

            holder.messageText.setBackgroundResource(R.drawable.message_background);

            holder.messageText.setTextColor(Color.WHITE);

            holder.messageText.setGravity(Gravity.LEFT);

        }

        holder.messageText.setText(messages.getMessage());
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public CircleImageView userProfileImage;

        public MessageViewHolder(View view){

            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text);
            userProfileImage = (CircleImageView) view.findViewById(R.id.message_profile_img);
        }
    }

}
