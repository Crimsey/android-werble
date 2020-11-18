package com.example.android_werble.entities;

import com.google.common.primitives.UnsignedInteger;

import java.util.Date;

public class User {
    UnsignedInteger user_id;
    //@SerializedName("name")
    String login;
    String email;
    String password;
    String first_name;
    String last_name;
    Date birth_date;
    String description;
    Double longitude;
    Double latitude;
    Boolean is_active;
    Boolean is_admin;

    public String getLogin(){
        return  login;
    }

}
