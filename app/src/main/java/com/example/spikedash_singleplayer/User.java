package com.example.spikedash_singleplayer;

public class User {
    public String username;
    public String email;
    private String uid;
    private String AccountID;
    private int balance;
    private int wins;
    private int highScore;
    public User() {
    }

    public User(String username, String email, String uid, String AccountID) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.AccountID = AccountID;
        this.balance = 0;
        this.wins =0;
        this.highScore =0;
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
    public void addWin(){ wins++; }
    public int getWins(){ return wins;}
    public void compareScore(int score){ if(score > highScore) highScore = score;}

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
}