package com.example.spikedash_singleplayer;

import android.app.Dialog;
import android.content.Context;

import android.graphics.Bitmap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.example.spikedash_singleplayer.Entitys.Bird;
import com.example.spikedash_singleplayer.Entitys.Candy;
import com.example.spikedash_singleplayer.Entitys.CountDown;
import com.example.spikedash_singleplayer.Entitys.Plus;
import com.example.spikedash_singleplayer.Entitys.Walls;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint bgPaint;
    private Bitmap backgroundBitmap;
    private Bird bird;
    private Candy candy;
    private Plus plus;
    private Walls walls;
    private CountDown countDown;

    private GameController controller;


    public GameView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        bgPaint = new Paint();
        bgPaint.setARGB(255, 240, 240, 240);

    }

    public void setGameController(GameController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && bird != null) {
            SoundManager.play("jump");
            bird.jump();
            return true;
        }
        return false;
    }


    public void drawSurface() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();

            if (backgroundBitmap != null) {
                canvas.drawBitmap(backgroundBitmap, 0, 0, null);
            } else {
                canvas.drawColor(Color.LTGRAY); // fallback
            }

            bird.draw(canvas);
            walls.draw(canvas);
            candy.draw(canvas);
            plus.draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void drawCountdown() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            canvas.drawPaint(bgPaint);
            bird.draw(canvas);
            walls.draw(canvas);
            candy.draw(canvas);
            plus.draw(canvas);
            countDown.draw(canvas);

            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void setBackgroundBitmap(Bitmap bitmap) {
        this.backgroundBitmap = Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(), true);
    }

    public void setBird(Bird bird) {
        this.bird = bird;
    }

    public void setCandy(Candy candy) {
        this.candy = candy;
    }

    public void setWalls(Walls walls) {
        this.walls = walls;
    }

    public void setPlus(Plus plus) {
        this.plus = plus;
    }

    public void setCountDown(CountDown countDown) {
        this.countDown = countDown;
    }
    public void setBirdBitmap(Bitmap birdBitmap) {
        if (this.bird != null) {
            this.bird.setBitmap(birdBitmap);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // controller.start() should be triggered from activity once bitmaps and user are ready
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}
}


