package com.example.spikedash_singleplayer;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bird extends Entity {

    private int velocity;

    public Bird(int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
        super(ScreenWidth, ScreenHeight, bitmap);
        x = ScreenWidth / 2 ;
        y = ScreenHeight / 2;
        velocity =1;

    }
    @Override
    public void move() {
        x+= 20;
        if (x > ScreenWidth){
            x = 0;
        }

    }
    public void jump(){
        y+= 20;
        if(y > ScreenHeight)
            y = 0;
    }

}
