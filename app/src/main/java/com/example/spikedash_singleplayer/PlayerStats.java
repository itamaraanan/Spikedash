package com.example.spikedash_singleplayer;
public class PlayerStats {
    private String username;
    private int highScore;
    private String uid;

    public PlayerStats() {}

    public PlayerStats(String username, int highScore, String uid) {
        this.username = username;
        this.highScore = highScore;
        this.uid = uid;
    }
    public String getUsername() { return username; }

    public int getHighScore() { return highScore; }
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

}
