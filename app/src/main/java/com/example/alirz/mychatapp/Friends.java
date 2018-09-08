package com.example.alirz.mychatapp;

public class Friends {

    String date,status,name,image;
    boolean online;

    public Friends(){}

    public Friends(String date, String status, String name, String image,boolean online) {
        this.date = date;
        this.status = status;
        this.name = name;
        this.image = image;
        this.online=online;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
