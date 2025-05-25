package com.example.spikedash_singleplayer;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VibrationManager {
    private static boolean isEnabled = true;

    public static void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public static void vibrate(Context context, int durationMs) {
        // Check if vibration is enabled before proceeding
        if (!isEnabled) return;

        // call the vibrate method with the specified duration
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(durationMs);
        }
    }

    public static void syncWithFirebase() {
        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        // Get the user's vibration setting from Firebase
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .child("settings")
                .child("vibration")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        // Update the local vibration setting based on Firebase value
                        boolean value = snapshot.getValue(Boolean.class);
                        setEnabled(value);
                    }
                });
    }
}

