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

public class StatsActivity extends AppCompatActivity {

    ImageButton btnReturn;
    User user;
    TextView balanceValue, gamesValue, highScoreValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        balanceValue = findViewById(R.id.balanceValue);
        gamesValue = findViewById(R.id.gamesValue);
        highScoreValue = findViewById(R.id.highScoreValue);
        btnReturn = findViewById(R.id.btnBack);
        btnReturn.setOnClickListener(v -> { finish(); });
        user = getIntent().getParcelableExtra("user");
        init();
    }

    private void init(){
        balanceValue.setText(String.valueOf(user.getBalance()));
        gamesValue.setText(String.valueOf(user.getGames()));
        highScoreValue.setText(String.valueOf(user.getHighScore()));
    }

}