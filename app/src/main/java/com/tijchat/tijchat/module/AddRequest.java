package com.tijchat.tijchat.module;

/**
 * Created by DELL PC on 21/05/2018.
 */

public class AddRequest {

    private String User_Name;
    private String User_status;
    private String User_image;

    public AddRequest(){

    }

    public AddRequest(String user_Name, String user_status, String user_image) {

        User_Name = user_Name;
        User_status = user_status;
        User_image = user_image;
    }


    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getUser_status() {
        return User_status;
    }

    public void setUser_status(String user_status) {
        User_status = user_status;
    }

    public String getUser_image() {
        return User_image;
    }

    public void setUser_image(String user_image) {
        User_image = user_image;
    }
}
