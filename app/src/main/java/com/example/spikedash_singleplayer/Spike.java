package com.example.spikedash_singleplayer;

import android.graphics.Bitmap;

public class Spike extends Entity{
    private boolean isRight;
    private float fading_speed = 0.1f;

    public Spike(int ScreenWidth, int ScreenHeight, Bitmap bitmap, boolean isRight) {
        super(ScreenWidth, ScreenHeight, bitmap);
        x = ScreenWidth;
        y = ScreenHeight;
        this.isRight = isRight;
    }

    @Override
    public void move() {
        if(isRight){
            x += fading_speed;
            if(x > ScreenWidth){
                x = 0;
            }
        }
        else {
            x -= fading_speed;
            if ((x < 0)) {
                x = ScreenWidth;
            }
        }
    }








}
