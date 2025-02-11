package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class GiftActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imgWheel);

    }

    public void buttonImageRouletteWheel(View view){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                float floatRandomRotation = new Random().nextFloat()*500;
                imageView.setRotation(floatRandomRotation);
                imageView.refreshDrawableState();

                float floatGetRotation = 10-(imageView.getRotation()%360)/36;
                floatGetRotation = (float) Math.ceil(floatGetRotation);
            }
        };
        Handler handler = new Handler(Looper.getMainLooper());
        for (int i=1; i<20; i++){
            handler.postDelayed(runnable, i*1000);
        }
    }
}