package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Bitmap;

import com.example.spikedash_singleplayer.Entitys.Entity;

public class Spike extends Entity {
    public Spike(int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
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
    public void move() {}

}
