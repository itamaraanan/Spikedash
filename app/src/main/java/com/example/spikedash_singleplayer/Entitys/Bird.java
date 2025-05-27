package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.HashMap;

public class Bird extends Entity {

    public boolean gameStarted;
    private float velocity;
    private final float gravity = 0.75f;
    private final float jumpStrength = -18f;
    private float floatOffset;
    private float floatSpeed;
    private float floatAmplitude;
    private float counter;
    private float horizontalSpeed;
    boolean Right = true;
    private float difficultyMultiplier;

    public Bird(int ScreenWidth, int ScreenHeight, Bitmap bitmap, float difficultyMultiplier ) {
        //constructor
        super(ScreenWidth, ScreenHeight, bitmap);

        x = ScreenWidth / 2 - (int) scaleX(50);
        y = ScreenHeight / 2 + (int) scaleY(50);

        this.difficultyMultiplier = difficultyMultiplier;
        floatOffset = 0;
        floatSpeed = 0.06f;
        floatAmplitude = scaleY(100);
        horizontalSpeed = scaleX(10)*difficultyMultiplier;

        gameStarted = false;
    }

    @Override
    public void move() {
        // This method updates the bird's position and handles its movement logic.
        if (!gameStarted) {
            // If the game hasn't started, the bird hovers in the middle of the screen.
            fly();
            y = ScreenHeight / 2 + (int) floatOffset;
        } else {
            // If the game has started, the bird moves horizontally and falls due to gravity.
            float gravityScaled = gravity * scaleY(1);

            if (Right) {
                // Move the bird to the right
                x += horizontalSpeed;
                velocity += gravityScaled;
                if (x + bitmap.getWidth() > ScreenWidth) {
                    // If the bird touches the right wall change its direction
                    x = ScreenWidth - bitmap.getWidth();
                    Right = false;
                }
            } else {
                // Move the bird to the left
                x -= horizontalSpeed;
                velocity += gravityScaled;
                if (x < 0) {
                    //If the bird touches the left wall change its direction
                    x = 0;
                    Right = true;
                }
            }
            // Apply vertical movement
            y += velocity;
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
        // This method makes the bird hover by applying a sine wave to its vertical position.
        counter += floatSpeed;
        floatOffset = (float) (floatAmplitude * Math.sin(counter));
    }

    public void jump() {
        if (!gameStarted) {
            gameStarted = true;
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
        // This method checks if the bird collides with another rectangular entity.
        // Calculate the center of the bird and its radius
        float birdCenterX = x + bitmap.getWidth() / 2.0f;
        float birdCenterY = y + bitmap.getHeight() / 2.0f;
        float birdRadius = Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2.0f;
        // Calculate the center of the other entity and its radius
        float otherCenterX = otherX + otherWidth / 2.0f;
        float otherCenterY = otherY + otherHeight / 2.0f;
        float otherRadius = Math.min(otherWidth, otherHeight) / 2.0f;
        // Calculate the distance between the centers of the bird and the other entity
        float distance = (float) Math.sqrt(Math.pow(birdCenterX - otherCenterX, 2) +
                Math.pow(birdCenterY - otherCenterY, 2));
        // Check if the distance is less than the sum of the entities' radii
        // and return true if they collide, false otherwise
        return distance < (birdRadius + otherRadius);
    }
}
