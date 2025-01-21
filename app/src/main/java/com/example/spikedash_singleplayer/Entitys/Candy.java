package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import java.util.*;

public class Candy extends Entity {

    private Random random;
    private float floatOffset;
    private float floatSpeed;
    private int floatAmplitude;
    private float counter;

    public Candy(int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
        super(ScreenWidth, ScreenHeight, bitmap);
        random = new Random();
        x = 75 + random.nextInt(ScreenWidth - 150);
        y = 275 + random.nextInt(ScreenHeight - 550);

        floatOffset = 0;
        floatSpeed = 0.05f;
        floatAmplitude = 10;
        counter = 0;
    }


    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y + floatOffset, null);
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    public void takesCandy() {
        x = 75 + random.nextInt(ScreenWidth - 150);
        y = 275 + random.nextInt(ScreenHeight - 550);

    }

    public void move() {
        counter += floatSpeed;
        floatOffset = (float) (floatAmplitude * Math.sin(counter));
    }
}
