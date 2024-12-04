package com.example.spikedash_singleplayer;

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

    public void move() {

    }

}
