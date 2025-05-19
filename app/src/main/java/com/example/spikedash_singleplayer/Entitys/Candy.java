package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import java.util.*;

public class Candy extends Entity {

    private Random random;
    private float floatOffset;
    private float floatSpeed;
    private float floatAmplitude;
    private float counter;

    public Candy(int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
        super(ScreenWidth, ScreenHeight, bitmap);

        random = new Random();

        // Use scaled margins for X and Y range
        int marginX = (int) scaleX(75);
        int marginYTop = (int) scaleY(275);
        int marginYBottom = (int) scaleY(550);

        x = marginX + random.nextInt(ScreenWidth - 2 * marginX);
        y = marginYTop + random.nextInt(ScreenHeight - marginYBottom);

        floatOffset = 0;
        floatSpeed = 0.05f;
        floatAmplitude = scaleY(10); // Scale float amplitude
        counter = 0;
    }

    @Override
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
        int marginX = (int) scaleX(75);
        int marginYTop = (int) scaleY(275);
        int marginYBottom = (int) scaleY(550);

        x = marginX + random.nextInt(ScreenWidth - 2 * marginX);
        y = marginYTop + random.nextInt(ScreenHeight - marginYBottom);
    }

    @Override
    public void move() {
        counter += floatSpeed;
        floatOffset = (float) (floatAmplitude * Math.sin(counter));
    }
}
