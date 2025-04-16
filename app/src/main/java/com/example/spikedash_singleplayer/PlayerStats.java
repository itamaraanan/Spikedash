package com.example.spikedash_singleplayer;
public class PlayerStats {
    private String username;
    private int highScore;
    private int wins;

    // Empty constructor (required by Firebase)
    public PlayerStats() {
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getWins() {
        return wins;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }
}
