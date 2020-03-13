package com.sanjay.openfire.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Awesome Pojo Generator
 */
public class MessageDefinedModel {
    @SerializedName("message")
    @Expose
    private Message message;

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}