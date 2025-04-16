package com.example.spikedash_singleplayer;
public class PlayerStats {
    private String username;
    private int games;
    private int highScore;
    private String uid;

    public PlayerStats() {}

    public PlayerStats(String username, int games, int highScore, String uid) {
        this.username = username;
        this.games = games;
        this.highScore = highScore;
        this.uid = uid;
    }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getGames() { return games; }
    public void setGames(int games) { this.games = games; }

    public int getHighScore() { return highScore; }
    public void setHighScore(int highScore) { this.highScore = highScore; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

}
