package com.example.spikedash_singleplayer.Managers;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsManager {
    public static void applySavedBgmVolume(Context context, String uid) {
        if (uid == null || uid.isEmpty()) return;

        DatabaseReference bgmRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("settings")
                .child("bgm");

        bgmRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                double volume = snapshot.getValue(Double.class);
                MusicManager.setVolume((float) volume);
            }
        });
    }

}
