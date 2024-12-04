package com.example.spikedash_singleplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameController extends SurfaceView  implements  Runnable{
    private int screenWidth;
    private int screenHeight;
    private Canvas canvas;
    private Bird bird;
    private Bitmap bitmapBird;
    private SurfaceHolder holder;
    private Paint bg;
    private Thread thread;

    public GameController(Context context, int screenWidth, int screenHeight) {
        super(context);

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        bg = new Paint();
        bg.setARGB(218, 218, 218, 218); //grey

        bitmapBird = BitmapFactory.decodeResource(getResources(), R.drawable.bird);
        bird = new Bird (screenWidth, screenHeight,bitmapBird);

        holder = getHolder();
        thread = new Thread(this);
        thread.start();

    }

    public void drawSurface() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            canvas.drawPaint(bg);
            bird.draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void run() {
        while(true){
            drawSurface();
            bird.move();
        }

    }
}
