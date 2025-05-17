package com.example.spikedash_singleplayer;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VibrationManager {
    private static boolean vibrationEnabled = true;

    public static void setEnabled(boolean enabled) {
        vibrationEnabled = enabled;
    }

    public static void vibrate(Context context, int milliseconds) {
        if (!vibrationEnabled) return;

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(milliseconds);
            }
        }
    }
    public static void syncWithFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .child("settings")
                .child("vibration");

        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                boolean isEnabled = snapshot.getValue(Boolean.class);
                setEnabled(isEnabled);
            }
        });
    }

}
