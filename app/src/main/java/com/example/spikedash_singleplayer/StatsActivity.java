package com.example.spikedash_singleplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StatsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnReturn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        btnReturn = findViewById(R.id.btnBack);
        btnReturn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btnReturn){
            Intent intent = new Intent(StatsActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}