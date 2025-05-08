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

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private SeekBar soundSeekBar, bgmSeekBar;
    private Switch vibrationSwitch;
    private LinearLayout btnHowToPlay, btnPrivacySettings, btnLogout, btnDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        btnBack = findViewById(R.id.btnBack);
        soundSeekBar = findViewById(R.id.soundSeekBar);
        bgmSeekBar = findViewById(R.id.bgmSeekBar);
        vibrationSwitch = findViewById(R.id.vibrationSwitch);
        btnHowToPlay = findViewById(R.id.btnHowToPlay);
        btnPrivacySettings = findViewById(R.id.btnCredits);
        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        btnBack.setOnClickListener(this);
        btnHowToPlay.setOnClickListener(this);
        btnPrivacySettings.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnDeleteAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if (v == btnLogout){
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        }
        if (v == btnDeleteAccount){
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                    .setNegativeButton("Cancel", null)
                    .show();
        }
        if (v == btnHowToPlay) {
            new AlertDialog.Builder(this)
                    .setTitle("How to Play")
                    .setMessage("1. Tap the screen to make the bird fly.\n\n"
                              + "2. Avoid obstacles and stay within the screen bounds.\n\n"
                              + "3. Collect candies to increase your score.\n\n"
                              + "4. Pause the game anytime using the pause button.\n\n"
                              + "5. Have fun and aim for a high score!")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }


    private void deleteAccount(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
}