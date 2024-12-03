package com.example.spikedash_singleplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameController extends SurfaceView {
    private int screetWidth;
    private int screenHeight;
    private Canvas canvas;
    private Bird bird;
    private Bitmap bitmapBird;
    private SurfaceHolder holder;

    public GameController(Context context, int screenWidth, int screenHeight) {
        super(context);

        this.screetWidth = screenWidth;
        this.screenHeight = screenHeight;

        bitmapBird = BitmapFactory.decodeResource(getResources(), R.drawable.bird);
        bird = new Bird (screetWidth, screenHeight,bitmapBird);

    }

    public void drawSurface(){
        if(holder.getSurface().isValid()){
            canvas = holder.lockCanvas();
            bird.draw(canvas );
            holder.unlockCanvasAndPost(canvas);
        }
    }
}
