package com.example.spikedash_singleplayer.Managers;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.spikedash_singleplayer.R;

import java.util.HashMap;

public class SoundManager {
    private static SoundPool soundPool;
    private static float globalVolume;
    private static HashMap<String, Integer> soundMap;

    public static void init(Context context) {
        // Initialize the SoundPool and load sounds

         globalVolume = 1.0f;

        // Set up AudioAttributes for the SoundPool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        // Create the SoundPool instance with the specified attributes
        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        // Load sounds into the soundMap
        soundMap = new HashMap<>();
        soundMap.put("jump", soundPool.load(context, R.raw.jump_sound, 1));
        soundMap.put("hit", soundPool.load(context, R.raw.hit_sound, 1));
        soundMap.put("candy", soundPool.load(context, R.raw.candy_sound, 1));
        soundMap.put("click", soundPool.load(context, R.raw.click_sound, 1));
        soundMap.put("select", soundPool.load(context, R.raw.select_sound, 1));
        soundMap.put("error", soundPool.load(context, R.raw.error_sound, 1));
        soundMap.put("win", soundPool.load(context, R.raw.win_sound, 1));
        soundMap.put("spin", soundPool.load(context, R.raw.wheel_sound, 1));
        soundMap.put("start", soundPool.load(context, R.raw.start_sound, 1));
    }

    public static void play(String sound) {
        // Play the sound if it exists in the soundMap
        if (soundMap.containsKey(sound)) {
            soundPool.play(soundMap.get(sound), globalVolume, globalVolume, 1, 0, 1f);
        }
    }

    public static void setVolume(float volume) {
    // Set the global volume for all sounds
        globalVolume = Math.max(0f, Math.min(volume, 1f));
    }
}
