package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.spikedash_singleplayer.Entitys.Entity;

public class Bird extends Entity {
    private float velocity;
    private final float gravity = 0.75f;
    private final float jumpStrength = -17.5f;

    boolean Right = true;

    public Bird(int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
        super(ScreenWidth, ScreenHeight, bitmap);
        x = ScreenWidth / 2 ;
        y = ScreenHeight / 2;

    }

    @Override
    public void move() {
        if (Right) {
            x += 10; // Move right
            velocity += gravity; // Apply gravity

            if (x > ScreenWidth - 144) { // Adjust boundary condition
                x = ScreenWidth - 144; // Keep bird within the right boundary
                Right = false; // Change direction
            }

            y += velocity; // Apply vertical movement
            if (y > ScreenHeight - 100) { // Prevent falling out of screen
                y = ScreenHeight - 100;
                velocity = 0; // Stop vertical movement
            }
        }
        else {
            x -= 10; // Move left
            velocity += gravity; // Apply gravity

            if (x < 0) { // Adjust boundary condition for left edge
                x = 0; // Keep bird within the left boundary
                Right = true; // Change direction
            }

            y += velocity; // Apply vertical movement
            if (y > ScreenHeight - 100) { // Prevent falling out of screen
                y = ScreenHeight - 100;
                velocity = 0; // Stop vertical movement
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save(); // Save the current canvas state

        if (!Right) {
            canvas.scale(-1, 1, x + bitmap.getWidth() / 2, y + bitmap.getHeight() / 2);
        }
        canvas.drawBitmap(bitmap, x, y, null);
        canvas.restore();
    }




    public void jump() {
        // Set the upward velocity for the jump
        velocity = jumpStrength;
    }

    public boolean collidesWith(int otherX, int otherY, int otherWidth, int otherHeight) {
        return (x < otherX + otherWidth &&
                x + bitmap.getWidth() > otherX &&
                y < otherY + otherHeight &&
                y + bitmap.getHeight() > otherY);
    }





}
