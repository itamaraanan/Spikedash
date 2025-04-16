package com.example.spikedash_singleplayer;
public class PlayerStats {
    private String username;
    private int wins;
    private int highScore;
    private String uid;

    public PlayerStats() {}

    public PlayerStats(String username, int wins, int highScore, String uid) {
        this.username = username;
        this.wins = wins;
        this.highScore = highScore;
        this.uid = uid;
    }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getHighScore() { return highScore; }
    public void setHighScore(int highScore) { this.highScore = highScore; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

}
