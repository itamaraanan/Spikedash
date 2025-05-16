package com.example.spikedash_singleplayer;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import java.util.HashMap;

public class SoundManager {
    private static SoundPool soundPool;
    private static float globalVolume = 1.0f;

    private static HashMap<String, Integer> soundMap;

    public static void init(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        soundMap = new HashMap<>();
        soundMap.put("jump", soundPool.load(context, R.raw.jump_sound, 1));
        soundMap.put("hit", soundPool.load(context, R.raw.hit_sound, 1));
        soundMap.put("candy", soundPool.load(context, R.raw.candy_sound, 1));
    }

    public static void play(String sound) {
        if (soundMap.containsKey(sound)) {
            soundPool.play(soundMap.get(sound), globalVolume, globalVolume, 1, 0, 1f);
        }
    }
    public static void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
    public static void setVolume(float volume) {
        globalVolume = Math.max(0f, Math.min(volume, 1f));
    }
}
