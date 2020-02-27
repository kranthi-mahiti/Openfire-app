package com.sanjay.openfire.chat.models;

public class MessageChatModel {

    private String from;
    private String to;
    private String message;
    private String message_time;
    private String avatat_intial;
    private String message_type;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_time() {
        return message_time;
    }

    public void setMessage_time(String message_time) {
        this.message_time = message_time;
    }

    public String getAvatat_intial() {
        return avatat_intial;
    }

    public void setAvatat_intial(String avatat_intial) {
        this.avatat_intial = avatat_intial;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }
}
