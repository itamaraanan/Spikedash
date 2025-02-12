package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout btnStart;
    ImageButton btnDifficulty;
    ImageButton btnGift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnDifficulty = findViewById(R.id.btnDifficulty);
        btnGift = findViewById(R.id.btnGift);
        btnDifficulty.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnGift.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btnStart) {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        }
        if(v == btnDifficulty){
            Intent intent = new Intent(MainActivity.this, DifficultyActivity.class);
            startActivity(intent);
        }
        if(v == btnGift){
            Intent intent = new Intent(MainActivity.this, GiftActivity.class);
            startActivity(intent);
        }
    }
}