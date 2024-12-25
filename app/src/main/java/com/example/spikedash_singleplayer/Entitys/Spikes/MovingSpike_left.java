package com.example.spikedash_singleplayer.Entitys.Spikes;

import android.graphics.Bitmap;

public class MovingSpike_left extends MovingSpike {
    public MovingSpike_left(int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
        super(ScreenWidth, ScreenHeight, bitmap);
        x = ScreenWidth;
        y = ScreenHeight;
    }

    @Override
    public void move() {
        x -= fading_speed;
        if (x < 0) {
            x = ScreenWidth;
        }
    }
}
