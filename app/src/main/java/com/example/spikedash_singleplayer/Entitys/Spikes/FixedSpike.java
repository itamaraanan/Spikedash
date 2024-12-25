package com.example.spikedash_singleplayer.Entitys.Spikes;

import android.graphics.Bitmap;

import com.example.spikedash_singleplayer.Entitys.Entity;

public class FixedSpike extends Entity {
    public FixedSpike(int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
        super(ScreenWidth, ScreenHeight, bitmap);
    }

    @Override
    public void move() {
        // Do nothing
    }
}
