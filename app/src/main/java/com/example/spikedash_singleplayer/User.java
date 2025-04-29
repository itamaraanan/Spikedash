package com.example.spikedash_singleplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public String username;
    public String email;
    private String uid;
    private String AccountID;
    private int balance;
    private int wins;
    private int highScore;
    private int games;
    private String base64Image; // Added base64Image field

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String username, String email, String uid, String AccountID) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.AccountID = AccountID;
        this.balance = 0;
        this.wins = 0;
        this.highScore = 0;
        this.games = 0;
        this.base64Image = null; // Initialize base64Image as null
    }

    protected User(Parcel in) {
        username = in.readString();
        email = in.readString();
        uid = in.readString();
        AccountID = in.readString();
        balance = in.readInt();
        wins = in.readInt();
        highScore = in.readInt();
        games = in.readInt();
        base64Image = in.readString(); // Read base64Image from parcel
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(uid);
        dest.writeString(AccountID);
        dest.writeInt(balance);
        dest.writeInt(wins);
        dest.writeInt(highScore);
        dest.writeInt(games);
        dest.writeString(base64Image); // Write base64Image to parcel
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

    // Getter and setter for base64Image
    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public void add(int amount) {
        this.balance += amount;
    }

    public void subtract(int amount) {
        this.balance -= amount;
    }

    public int getBalance() {
        return this.balance;
    }

    public void addWin() {
        wins++;
    }

    public int getWins() {
        return wins;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccountID() {
        return AccountID;
    }

    public void setAccountID(String accountID) {
        AccountID = accountID;
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