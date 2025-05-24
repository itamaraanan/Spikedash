package com.example.spikedash_singleplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String username;
    private String email;
    private String uid;
    private int balance;
    private int highScore;
    private int games;
    private String base64Image;

    public User() {
        // Required empty constructor for Firebase
    }

    // Constructor for creating a User object with all fields
    public User(String username, String email, String uid) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.balance = 0;
        this.highScore = 0;
        this.games = 0;
        this.base64Image = null;
    }

    //constructor for creating a User from a Parcel
    protected User(Parcel in) {
        username = in.readString();
        email = in.readString();
        uid = in.readString();
        balance = in.readInt();
        highScore = in.readInt();
        games = in.readInt();
        base64Image = in.readString();
    }

    @Override
    // Method to write the User object to a Parcel
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(uid);
        dest.writeInt(balance);
        dest.writeInt(highScore);
        dest.writeInt(games);
        dest.writeString(base64Image);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public void add(int amount) {
        this.balance += amount;
    }

    public int getBalance() {
        return this.balance;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGames() {
        return games;
    }

    public void addGame() {
        games++;
    }
    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }
}