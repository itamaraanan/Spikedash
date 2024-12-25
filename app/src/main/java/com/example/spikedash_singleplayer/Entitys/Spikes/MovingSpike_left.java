package com.example.spikedash_singleplayer.Entitys.Spikes;

import android.graphics.Bitmap;

public class MovingSpike_left extends MovingSpike {
    public MovingSpike_left(int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
        super(ScreenWidth, ScreenHeight, bitmap);
        x = ScreenWidth;
        y = ScreenHeight;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return bitmap.getWidth(); // Get width of the bitmap
    }

    public int getHeight() {
        return bitmap.getHeight(); // Get height of the bitmap
    }


    @Override
    public void move() {
        x -= fading_speed;
        if (x < 0) {
            x = ScreenWidth;
        }
    }
}
