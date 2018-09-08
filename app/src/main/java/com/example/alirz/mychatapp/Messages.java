package com.example.alirz.mychatapp;

public class Messages {

    String message,type,from;
    boolean seen;
    long time;

    public Messages(){

    }

    public Messages(String message, String type, boolean seen, long time,String from) {
        this.message = message;
        this.type = type;
        this.seen = seen;
        this.time = time;
        this.from=from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
