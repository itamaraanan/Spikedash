package com.example.spikedash_singleplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnBack;
    TextView winsTab,highScoreTab;
    View winsInidcator,highScoreIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        btnBack = findViewById(R.id.btnBack);
        winsTab = findViewById(R.id.winsTab);
        winsInidcator = findViewById(R.id.winsIndicator);
        highScoreIndicator = findViewById(R.id.highScoreIndicator);
        highScoreTab = findViewById(R.id.highScoreTab);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == winsTab) {
            winsTab.setAlpha(1f);
            highScoreTab.setAlpha(0.5f);

            winsInidcator.setVisibility(View.VISIBLE);
            highScoreIndicator.setVisibility(View.INVISIBLE);
        }
        if (v == highScoreTab) {
            highScoreTab.setAlpha(1f);
            winsTab.setAlpha(0.5f);
            highScoreTab.setVisibility(View.VISIBLE);
            winsInidcator.setVisibility(View.INVISIBLE);
        }
        if (v == btnBack) {
            Intent intent = new Intent(LeaderboardActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}