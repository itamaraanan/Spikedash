package com.example.spikedash_singleplayer;

import android.media.MediaPlayer;
import android.content.Context;

public class MusicManager {
    public static MediaPlayer mediaPlayer;
    public static boolean isPlaying = false;

    public static void start(Context context, int soundResource) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.getApplicationContext(), soundResource);
            mediaPlayer.setLooping(true);
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }
    public static void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public static void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && isPlaying) {
            mediaPlayer.start();
        }
    }

    public static void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }

    public static void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }
}