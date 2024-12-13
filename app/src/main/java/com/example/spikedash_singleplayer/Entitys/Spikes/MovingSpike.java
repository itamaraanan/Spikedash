package com.example.spikedash_singleplayer.Entitys.Spikes;

import android.graphics.Bitmap;

import com.example.spikedash_singleplayer.Entitys.Entity;

public abstract class MovingSpike extends Entity {
    protected float fading_speed = 0.1f;
    public MovingSpike(int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
        super(ScreenWidth, ScreenHeight, bitmap);
        x = ScreenWidth;
        y = ScreenHeight;
    }

    public abstract void move();

}
