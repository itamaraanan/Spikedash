package com.example.spikedash_singleplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private SeekBar soundSeekBar, bgmSeekBar;
    private Switch vibrationSwitch;
    private LinearLayout btnHowToPlay, btnPrivacySettings, btnLogout, btnDeleteAccount;
    private FirebaseUser user;
    private DatabaseReference settingsRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        uid = user.getUid();
        settingsRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("settings");

        btnBack = findViewById(R.id.btnBack);
        soundSeekBar = findViewById(R.id.seekbar_sound);
        bgmSeekBar = findViewById(R.id.seekbar_bgm);
        vibrationSwitch = findViewById(R.id.switch_vibrartion);
        btnHowToPlay = findViewById(R.id.btnHowToPlay);
        btnPrivacySettings = findViewById(R.id.btnCredits);
        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        btnBack.setOnClickListener(this);
        btnHowToPlay.setOnClickListener(this);
        btnPrivacySettings.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnDeleteAccount.setOnClickListener(this);

        soundSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) settingsRef.child("sound").setValue(progress / 100.0);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        bgmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float volume = progress / 100.0f;
                    settingsRef.child("bgm").setValue(volume);
                    MusicManager.setVolume(volume);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            VibrationManager.vibrate(this, 25);
            settingsRef.child("vibration").setValue(isChecked);
        });

        loadSettings();
    }

    private void loadSettings() {
        settingsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot snapshot = task.getResult();
                double sound = snapshot.child("sound").getValue(Double.class) != null ? snapshot.child("sound").getValue(Double.class) : 1.0;
                double bgm = snapshot.child("bgm").getValue(Double.class) != null ? snapshot.child("bgm").getValue(Double.class) : 1.0;
                boolean vibration = snapshot.child("vibration").getValue(Boolean.class) != null && snapshot.child("vibration").getValue(Boolean.class);

                soundSeekBar.setProgress((int) (sound * 100));
                bgmSeekBar.setProgress((int) (bgm * 100));
                vibrationSwitch.setChecked(vibration);
            }
        });
    }

    private void deleteAccount() {
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MenuActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Error deleting account", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        VibrationManager.vibrate(this, 25);
        if (v == btnBack) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (v == btnLogout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MenuActivity.class));
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            finish();
        } else if (v == btnDeleteAccount) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                    .setNegativeButton("Cancel", null)
                    .show();
        } else if (v == btnHowToPlay) {
            new AlertDialog.Builder(this)
                    .setTitle("How to Play")
                    .setMessage(
                            "1. Tap the screen to make the bird fly.\n\n" +
                                    "2. Avoid obstacles and stay within the screen bounds.\n\n" +
                                    "3. Collect candies to increase your score.\n\n" +
                                    "4. Pause the game anytime using the pause button.\n\n" +
                                    "5. Have fun and aim for a high score!"
                    )
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}
