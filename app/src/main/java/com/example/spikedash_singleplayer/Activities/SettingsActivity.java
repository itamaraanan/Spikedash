package com.example.spikedash_singleplayer.Activities;

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

import com.example.spikedash_singleplayer.Activities.MenuActivity;
import com.example.spikedash_singleplayer.MusicManager;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.SoundManager;
import com.example.spikedash_singleplayer.VibrationManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnBack;
    SeekBar soundSeekBar, bgmSeekBar;
    Switch vibrationSwitch;
    LinearLayout btnHowToPlay, btnLogout, btnDeleteAccount;
    FirebaseUser user;
    DatabaseReference settingsRef;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            SoundManager.play("error");
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
        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        btnBack.setOnClickListener(this);
        btnHowToPlay.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnDeleteAccount.setOnClickListener(this);

        soundSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update sound setting in Firebase
                if (fromUser) {
                    settingsRef.child("sound").setValue(progress / 100.0);
                    SoundManager.setVolume(progress / 100.0f);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        bgmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Update BGM setting in Firebase
                    float volume = progress / 100.0f;
                    settingsRef.child("bgm").setValue(volume);
                    MusicManager.setVolume(volume);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update vibration setting in Firebase
            VibrationManager.setEnabled(isChecked);
            VibrationManager.vibrate(this, 25);
            SoundManager.play("click");
            settingsRef.child("vibration").setValue(isChecked);
        });

        loadSettings();
    }

    private void loadSettings() {
        // Load settings from Firebase
        settingsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot snapshot = task.getResult();

                // Set the SeekBar and Switch values based on Firebase data
                double sound = snapshot.child("sound").getValue(Double.class) != null ? snapshot.child("sound").getValue(Double.class) : 1.0;
                double bgm = snapshot.child("bgm").getValue(Double.class) != null ? snapshot.child("bgm").getValue(Double.class) : 1.0;
                boolean vibration = snapshot.child("vibration").getValue(Boolean.class) != null && snapshot.child("vibration").getValue(Boolean.class);

                soundSeekBar.setProgress((int) (sound * 100));
                bgmSeekBar.setProgress((int) (bgm * 100));
                vibrationSwitch.setChecked(vibration);

                SoundManager.setVolume((float) sound);
                MusicManager.setVolume((float) bgm);
                VibrationManager.setEnabled(vibration);

            }
        });
    }

    private void deleteAccount() {
        if (user != null) {
            // Delete user from Firebase Authentication
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MenuActivity.class));
                    finish();
                } else {
                    SoundManager.play("error");
                    Toast.makeText(this, "Error deleting account", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        VibrationManager.vibrate(this, 25);
        SoundManager.play("click");

        if (v == btnBack) {
            setResult(RESULT_OK);
            finish();
        } else if (v == btnLogout) {
            // Log out the user
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MenuActivity.class));
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            finish();
            MusicManager.stop();
            MusicManager.release();
        } else if (v == btnDeleteAccount) {
            // Show confirmation dialog for account deletion
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        MusicManager.stop();
                        MusicManager.release();
                        deleteAccount();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else if (v == btnHowToPlay) {
            // Show how to play dialog
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
