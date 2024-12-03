package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class GameActivity extends AppCompatActivity {

    FrameLayout frm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        frm = findViewById(R.id.frm);

        View rootLayout = findViewById(R.id.root_layout);
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("ScreenTouch", "Screen was touched!");
                    return true;
                }
                return false;
            }
        });
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