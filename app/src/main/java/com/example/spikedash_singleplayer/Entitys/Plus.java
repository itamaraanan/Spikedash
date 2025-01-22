package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Plus extends Entity {
    private int alpha;
    private boolean active;
    private Paint paint;

    public Plus(int screenWidth, int screenHeight, Bitmap bitmap) {
        super(screenWidth, screenHeight, bitmap);
        this.alpha = 255;
        this.active = false;
        this.paint = new Paint();
    }

    @Override
    public void move() {
        if (active) {
            y -= 5;
            alpha -= 7;
            if (alpha <= 0) {
                alpha = 0;
                active = false;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (active) {
            paint.setAlpha(alpha);
            canvas.drawBitmap(bitmap, x, y, paint);
        }
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void activate(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.alpha = 255;
        this.active = true;
    }

    public int getBitmapWidth() {
        return bitmap.getWidth();
    }

    public int getBitmapHeight() {
        return bitmap.getHeight();
    }

    public boolean isActive() {
        return active;
    }
}
