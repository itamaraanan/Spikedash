package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DifficultyActivity extends AppCompatActivity implements View.OnClickListener {

    Button easyButton;
    Button mediumButton;
    Button hardButton;
    Button insaneButton;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        easyButton = findViewById(R.id.easyButton);
        mediumButton = findViewById(R.id.mediumButton);
        hardButton = findViewById(R.id.hardButton);
        insaneButton = findViewById(R.id.insaneButton);
        easyButton.setOnClickListener(this);
        mediumButton.setOnClickListener(this);
        hardButton.setOnClickListener(this);
        insaneButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        VibrationManager.vibrate(this, 25);
        if (v == easyButton) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("difficulty", "easy");
            startActivity(intent);
        } else if (v == mediumButton) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("difficulty", "medium");
            startActivity(intent);
        } else if (v == hardButton) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("difficulty", "hard");
            startActivity(intent);
        } else if (v == insaneButton) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("difficulty", "insane");
            startActivity(intent);
        }
    }
}