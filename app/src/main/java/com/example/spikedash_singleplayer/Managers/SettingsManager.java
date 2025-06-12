package com.example.spikedash_singleplayer.Managers;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsManager {
    public static void applySavedBgmVolume( String uid) {
        if (uid == null || uid.isEmpty()) return;
        // Retrieve the BGM volume setting from Firebase Realtime Database
        DatabaseReference bgmRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("settings")
                .child("bgm");
        // Get the BGM volume value and set it in MusicManager
        bgmRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                double volume = snapshot.getValue(Double.class);
                MusicManager.setVolume((float) volume);
            }
        });
    }

    public static void applySavedSoundVolume(String uid) {
        if (uid == null || uid.isEmpty()) return;
        // Retrieve the BGM volume setting from Firebase Realtime Database
        DatabaseReference bgmRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("settings")
                .child("sound");
        // Get the BGM volume value and set it in MusicManager
        bgmRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                double volume = snapshot.getValue(Double.class);
                MusicManager.setVolume((float) volume);
            }
        });
    }

}
