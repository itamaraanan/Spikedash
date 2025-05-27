package com.example.spikedash_singleplayer.Managers;

import android.media.MediaPlayer;
import android.content.Context;

public class MusicManager {
    public static MediaPlayer mediaPlayer;
    public static boolean isPlaying = false;


    public static void start(Context context, int soundResource) {
        //starts the music if it is not already playing
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
        //stops the music if it is playing
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public static void release() {
        //releases the media player resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }

    public static void setVolume(float volume) {
        //sets the volume of the media player
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }
}