package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.spikedash_singleplayer.Entitys.Entity;

public class Bird extends Entity {

    public boolean gameSrarted;
    private float velocity;
    private final float gravity = 0.7f;
    private final float jumpStrength = -17.5f;
    private float floatOffset;
    private float floatSpeed;
    private int floatAmplitude;
    private float counter;


    boolean Right = true;

    public Bird(int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
        super(ScreenWidth, ScreenHeight, bitmap);
        x = ScreenWidth / 2 - 50;
        y = ScreenHeight / 2 + 50;
        floatOffset = 0;
        floatSpeed = 0.06f;
        floatAmplitude = 100;
        gameSrarted = false;
    }

    @Override
    public void move() {
        if (!gameSrarted) {
            fly();
            y = ScreenHeight / 2 + (int) floatOffset;
        }
        else {
            if (Right) {
                x += 10;
                velocity += gravity;
                if (x + bitmap.getWidth() > ScreenWidth) {
                    x = ScreenWidth - bitmap.getWidth();
                    Right = false;
                }
            } else {
                x -= 10;
                velocity += gravity;
                if (x < 0) {
                    x = 0;
                    Right = true;
                }
            }

            y += velocity;
            if (y > ScreenHeight - bitmap.getHeight()) {
                y = ScreenHeight - bitmap.getHeight();
                velocity = 0;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        if (!Right) {
            canvas.scale(-1, 1, x + bitmap.getWidth() / 2 , y + bitmap.getHeight() / 2);
        }
        canvas.drawBitmap(bitmap, x, y, null);
        canvas.restore();
    }



    public void fly() {
        counter += floatSpeed;
        floatOffset = (float) (floatAmplitude * Math.sin(counter));
    }


    public void jump() {
        if (!gameSrarted) {
            gameSrarted = true;
        }
        velocity = jumpStrength;
    }

    public boolean collidesWith(int otherX, int otherY, int otherWidth, int otherHeight) {
        float birdCenterX = x + bitmap.getWidth() / 2.0f;
        float birdCenterY = y + bitmap.getHeight() / 2.0f;
        float birdRadius = Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2.0f;

        float otherCenterX = otherX + otherWidth / 2.0f;
        float otherCenterY = otherY + otherHeight / 2.0f;
        float otherRadius = Math.min(otherWidth, otherHeight) / 2.0f;
        float distance = (float) Math.sqrt(Math.pow(birdCenterX - otherCenterX, 2) +
                Math.pow(birdCenterY - otherCenterY, 2));
        return distance < (birdRadius + otherRadius);
    }






}
