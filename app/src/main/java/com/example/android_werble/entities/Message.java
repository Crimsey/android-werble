package com.example.android_werble.entities;

import com.squareup.moshi.Json;

public class Message {

    @Json(name = "message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
