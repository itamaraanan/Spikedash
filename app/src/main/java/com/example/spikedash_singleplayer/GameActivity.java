package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {
    FrameLayout frm;
    GameController gameController;
    TextView tvScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tvScore = findViewById(R.id.tvScore);
        frm = findViewById(R.id.frm);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && gameController == null) {  // Ensure it only runs once
            int w = frm.getWidth();
            int h = frm.getHeight();

            gameController = new GameController(this, w, h, tvScore);
            frm.addView(gameController);
        }
    }

    public void jump(View view) {

    }
}
