package com.example.spikedash_singleplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

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
        }
        if (v == btnDeleteAccount){
            
        }
    }
}