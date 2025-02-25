package com.example.spikedash_singleplayer;

public class User {
    public String username;
    public String email;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}