package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import java.util.*;

public class Candy extends Entity {

    private int score;
    private Random random;
    private float floatOffset;
    private float floatSpeed;
    private int floatAmplitude;
    private float counter;

    public Candy(int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
        super(ScreenWidth, ScreenHeight, bitmap);
        score = 0;
        random = new Random();
        x = 20 + random.nextInt(1060);
        y = 100 + random.nextInt(2300);

        floatOffset = 0;
        floatSpeed = 0.05f;
        floatAmplitude = 10;
        counter = 0;
    }

    public int getScore() {
        return score;
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
        score++;
        x = 40 + random.nextInt(1080 - 80);
        y = 100 + random.nextInt(1920 - 200);
    }

    public void move() {
        counter += floatSpeed;
        floatOffset = (float) (floatAmplitude * Math.sin(counter));
    }
}
