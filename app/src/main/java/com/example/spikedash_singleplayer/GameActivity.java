package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

public class GameActivity extends AppCompatActivity {

    FrameLayout frm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        frm = findViewById(R.id.frm);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            int w = frm.getWidth();
            int h = frm.getHeight();
            Log.d("GameActivity", "Width: " + w + " Height: " + h);
        }
    }

}