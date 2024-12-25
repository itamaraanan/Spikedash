package com.example.spikedash_singleplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.spikedash_singleplayer.Entitys.Bird;
import com.example.spikedash_singleplayer.Entitys.Walls;

public class GameController extends SurfaceView  implements  Runnable{
    private int screenWidth;
    private int screenHeight;
    private Canvas canvas;
    private Bird bird;

    private Walls walls;
    private Bitmap bitmapBird;
    private Bitmap spikeBitmap;
    private SurfaceHolder holder;
    private Paint bg;
    private Thread thread;

    public GameController(Context context, int screenWidth, int screenHeight) {
        super(context);

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        bg = new Paint();
        bg.setARGB(218, 218, 218, 218); //grey

        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bird);
        spikeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.left_spike);
        walls = new Walls(screenWidth, screenHeight, spikeBitmap);
        bitmapBird = Bitmap.createScaledBitmap(originalBitmap, 144, 100, false);
        bird = new Bird (screenWidth, screenHeight,bitmapBird);

        holder = getHolder();
        thread = new Thread(this);
        thread.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            bird.jump();
            return true;
        }
        return false;
    }


    public void drawSurface() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            canvas.drawPaint(bg);
            bird.draw(canvas);
            walls.draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void run() {
        while (true) {
            drawSurface();
            // Check if bird touches the left or right wall
            if (bird.getX() <= 0 && walls.isLeftWallActive()) {
                walls.switchWall(); // Switch to the right wall

            } else if (bird.getX() >= screenWidth - 144 && !walls.isLeftWallActive()) {
                walls.switchWall(); // Switch to the left wall
            }
            bird.move();
        }
    }





}
