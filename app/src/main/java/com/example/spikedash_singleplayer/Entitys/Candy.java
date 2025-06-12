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

    //constructor
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
        int candyWidth = (int) scaleX(100);
        int candyHeight = (int) scaleY(100);
        int floatRange = (int) scaleY(100);

        int spikeSafeMarginX = (int) scaleX(150);
        int wallMarginY = (int) scaleY(250);
        int safetyBuffer = (int) scaleY(30);

        int marginX = spikeSafeMarginX + candyWidth / 2;
        x = marginX + random.nextInt(ScreenWidth - 2 * marginX);

        int marginYTop = wallMarginY + floatRange + candyHeight + safetyBuffer;
        int marginYBottom = ScreenHeight - wallMarginY - floatRange - candyHeight - safetyBuffer;

        y = marginYTop + random.nextInt(Math.max(1, marginYBottom - marginYTop));
    }






    @Override
    public void move() {
        // Update the floating effect of the candy
        counter += floatSpeed;
        floatOffset = (float) (floatAmplitude * Math.sin(counter));
    }
}
