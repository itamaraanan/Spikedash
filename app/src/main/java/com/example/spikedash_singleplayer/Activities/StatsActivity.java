package com.example.spikedash_singleplayer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.Managers.SoundManager;
import com.example.spikedash_singleplayer.User;
import com.example.spikedash_singleplayer.Managers.VibrationManager;

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
        btnReturn.setOnClickListener(v -> {
            VibrationManager.vibrate(this, 25);
            SoundManager.play("click");
            // Return to MainActivity
            Intent intent = new Intent(StatsActivity.this, MainActivity.class);
            startActivity(intent);
        });
        user = getIntent().getParcelableExtra("user");
        init();
    }

    private void init(){
        // Set the values in the TextViews with user data
        balanceValue.setText(String.valueOf(user.getBalance()));
        gamesValue.setText(String.valueOf(user.getGames()));
        highScoreValue.setText(String.valueOf(user.getHighScore()));
    }

}