package com.example.alirz.mychatapp;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

    private String name, user_id, status,image,token_id;

    public User(){

    }


    public User(String name, String user_id, String status, String image, String token_id) {
        this.name = name;
        this.user_id = user_id;
        this.status = status;
        this.image = image;
        this.token_id = token_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    protected User(Parcel in) {
        name = in.readString();
        user_id = in.readString();
        status = in.readString();
        image = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(user_id);
        dest.writeString(status);
        dest.writeString(image);
    }
}
