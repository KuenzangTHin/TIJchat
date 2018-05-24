package com.tijchat.tijchat.module;

/**
 * Created by DELL PC on 18/05/2018.
 */

public class Users {

    public String User_Name;
    public String User_image;
    public String User_status;
    public String User_thumb_image;

    public Users(){

    }

    public Users(String user_Name, String user_image, String user_status, String user_thumb_image) {
        User_Name = user_Name;
        User_image = user_image;
        User_status = user_status;
        this.User_thumb_image = user_thumb_image;

    }

    public String getUser_thumb_image() {
        return User_thumb_image;
    }

    public void setUser_thumb_image(String user_thumb_image) {
        User_thumb_image = user_thumb_image;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getUser_image() {
        return User_image;
    }

    public void setUser_image(String user_image) {
        User_image = user_image;
    }

    public String getUser_status() {
        return User_status;
    }

    public void setUser_status(String user_status) {
        User_status = user_status;
    }
}
