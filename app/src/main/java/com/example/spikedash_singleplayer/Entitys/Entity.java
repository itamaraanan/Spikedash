package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public abstract class Entity {
    protected int x;
    protected int y;
    protected int ScreenWidth;
    protected int ScreenHeight;
    protected Bitmap bitmap;

    public Entity( int ScreenWidth, int ScreenHeight, Bitmap bitmap) {
        this.ScreenWidth = ScreenWidth;
        this.ScreenHeight = ScreenHeight;
        this.bitmap = bitmap;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);

    }


    protected float scaleX(float value) {
        return value * (ScreenWidth / 1080f);
    }

    protected float scaleY(float value) {
        return value * (ScreenHeight / 1920f);
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public abstract void move();

}
