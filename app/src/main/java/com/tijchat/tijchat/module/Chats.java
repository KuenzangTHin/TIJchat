package com.tijchat.tijchat.module;

import com.tijchat.tijchat.View.CHat;

/**
 * Created by DELL PC on 21/05/2018.
 */

public class Chats {

    private String User_status;

    public Chats(){

    }

    public Chats(String user_status) {
        User_status = user_status;
    }

    public String getUser_status() {
        return User_status;
    }

    public void setUser_status(String user_status) {
        User_status = user_status;
    }
}
