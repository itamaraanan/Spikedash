package com.example.spikedash_singleplayer;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bird extends Entity {

    public Bird(int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
        super(ScreenWidth, ScreenHeight, bitmap);
        x = ScreenWidth / 2 ;
        y = ScreenHeight / 2;

    }
    @Override
    public void draw(Canvas canvas) {

    }


}
