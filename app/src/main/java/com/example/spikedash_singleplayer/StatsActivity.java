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

public class StatsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnReturn;
    User user;
    TextView winningsValue;
    TextView gamesValue;
    TextView highScoreValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        winningsValue = findViewById(R.id.winningsValue);
        gamesValue = findViewById(R.id.gamesValue);
        highScoreValue = findViewById(R.id.highScoreValue);
        btnReturn = findViewById(R.id.btnBack);
        user = getIntent().getParcelableExtra("user");
        btnReturn.setOnClickListener(this);
        init();
    }

    public void init(){
        winningsValue.setText(String.valueOf(user.getWins()));
        gamesValue.setText(String.valueOf(user.getGames()));
        highScoreValue.setText(String.valueOf(user.getHighScore()));
    }

    @Override
    public void onClick(View v) {
        if(v == btnReturn){
            Intent intent = new Intent(StatsActivity.this, MainActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }
}