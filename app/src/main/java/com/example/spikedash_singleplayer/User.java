package com.example.spikedash_singleplayer;

public class User {
    public String username;
    public String email;
    private int balance;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.balance = 0;
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


}