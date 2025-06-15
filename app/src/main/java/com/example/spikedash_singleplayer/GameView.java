package com.example.spikedash_singleplayer;

import android.content.Context;

import android.graphics.Bitmap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spikedash_singleplayer.Entitys.Bird;
import com.example.spikedash_singleplayer.Entitys.Candy;
import com.example.spikedash_singleplayer.Entitys.CountDown;
import com.example.spikedash_singleplayer.Entitys.Plus;
import com.example.spikedash_singleplayer.Entitys.Walls;
import com.example.spikedash_singleplayer.Managers.SoundManager;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Canvas canvas;
    private Bitmap backgroundBitmap;
    private Bird bird;
    private Candy candy;
    private Plus plus;
    private Walls walls;
    private CountDown countDown;
    private boolean touched = false;
    private GameController controller;


    public GameView(Context context) {
        // Initialize the SurfaceView and its holder
        super(context);
        holder = getHolder();
        holder.addCallback(this);
    }

    public void setGameController(GameController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Jump the bird when the screen is touched
        if (event.getAction() == MotionEvent.ACTION_DOWN && bird != null) {
            SoundManager.play("jump");
            bird.jump();
            //handle first touch
            if (!touched) {
                touched = true;
                if (controller != null) {
                    controller.onFirstTouch();
                }
            }
            return true;
        }

        return false;
    }


    public void drawSurface() {
        // Draw the game elements on the surface
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
        // Draw the countdown on the canvas
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();

            if (backgroundBitmap != null) {
                canvas.drawBitmap(backgroundBitmap, 0, 0, null);
            } else {
                canvas.drawColor(Color.LTGRAY);
            }
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // controller.start() should be triggered from activity once bitmaps and user are ready
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}
}


