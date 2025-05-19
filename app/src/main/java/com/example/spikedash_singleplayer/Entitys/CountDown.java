package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class CountDown extends Entity {

    private int number;
    private float scale;
    private int alpha;
    private boolean isFinished;
    private Paint paint;

    public CountDown(int ScreenWidth, int ScreenHeight, Bitmap bitmap, int number) {
        super(ScreenWidth, ScreenHeight, bitmap);
        this.number = number;
        this.x = ScreenWidth / 2;
        this.y = ScreenHeight / 2;
        this.scale = 1.0f;
        this.alpha = 255;
        this.isFinished = false;

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(scaleY(200)); // base text size
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        paint.setTextSize(scaleY(200) * scale); // scalable font
        paint.setAlpha(alpha);
        canvas.drawText(String.valueOf(number), x, y, paint);
    }

    @Override
    public void move() {
        scale += 0.05f;
        alpha -= 5;
        if (alpha <= 0) {
            isFinished = true;
        }
    }

    public boolean isFinished() {
        return isFinished;
    }
}
