package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bird extends Entity {

    public boolean gameSrarted;
    private float velocity;
    private final float gravity = 0.75f;
    private final float jumpStrength = -17f;
    private float floatOffset;
    private float floatSpeed;
    private float floatAmplitude;
    private float counter;
    private float horizontalSpeed;

    boolean Right = true;

    public Bird(int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
        super(ScreenWidth, ScreenHeight, bitmap);

        x = ScreenWidth / 2 - (int) scaleX(50);
        y = ScreenHeight / 2 + (int) scaleY(50);

        floatOffset = 0;
        floatSpeed = 0.06f;
        floatAmplitude = scaleY(100);
        horizontalSpeed = scaleX(10);

        gameSrarted = false;
    }

    @Override
    public void move() {
        if (!gameSrarted) {
            fly();
            y = ScreenHeight / 2 + (int) floatOffset;
        } else {
            float gravityScaled = gravity * scaleY(1);

            if (Right) {
                x += horizontalSpeed;
                velocity += gravityScaled;
                if (x + bitmap.getWidth() > ScreenWidth) {
                    x = ScreenWidth - bitmap.getWidth();
                    Right = false;
                }
            } else {
                x -= horizontalSpeed;
                velocity += gravityScaled;
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
            canvas.scale(-1, 1, x + bitmap.getWidth() / 2f, y + bitmap.getHeight() / 2f);
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

    public void increaseSpeed() {
        float maxSpeed = scaleX(20);
        if (horizontalSpeed < maxSpeed) {
            horizontalSpeed += scaleX(0.2f);
        }
    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getWidth() {
        return bitmap != null ? bitmap.getWidth() : 0;
    }

    public int getHeight() {
        return bitmap != null ? bitmap.getHeight() : 0;
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
