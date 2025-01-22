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
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.spikedash_singleplayer.Entitys.Bird;
import com.example.spikedash_singleplayer.Entitys.Candy;
import com.example.spikedash_singleplayer.Entitys.Plus;
import com.example.spikedash_singleplayer.Entitys.Spikes.MovingSpike_left;
import com.example.spikedash_singleplayer.Entitys.Spikes.MovingSpike_right;
import com.example.spikedash_singleplayer.Entitys.Walls;

public class GameController extends SurfaceView implements Runnable, View.OnClickListener {
    private TextView score;
    private int screenWidth;
    private int screenHeight;
    private ImageButton pause;
    private Canvas canvas;
    private Bird bird;
    private Candy candy;
    private Plus plus;
    private Walls walls;
    private Bitmap bitmapBird;
    private Bitmap spikeBitmap;
    private Bitmap candyBitmap;
    private Bitmap plusBitmap;
    private SurfaceHolder holder;
    private Paint bg;
    private int candies;
    private Thread thread;

    public GameController(Context context, int screenWidth, int screenHeight, TextView score) {
        super(context);
        this.score = score;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        bg = new Paint();
        bg.setARGB(218, 218, 218, 218); //grey

        Bitmap originalBirdBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bird);
        candyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.candy96);
        spikeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.left_spike);
        plusBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
        walls = new Walls(screenWidth, screenHeight, spikeBitmap);
        bitmapBird = Bitmap.createScaledBitmap(originalBirdBitmap, 144, 100, false);
        bird = new Bird (screenWidth, screenHeight,bitmapBird);
        candyBitmap = Bitmap.createScaledBitmap(candyBitmap, 96, 96, false);
        candy = new Candy(screenWidth, screenHeight, candyBitmap);
        plusBitmap = Bitmap.createScaledBitmap(plusBitmap, 600, 600, false);
        plus = new Plus(screenWidth, screenHeight, plusBitmap);
        plus.setX(candy.getX());
        plus.setY(candy.getY());
        score  = findViewById(R.id.tvScore);

        pause = findViewById(R.id.imbPause);
        candies =0;
        //pause.setOnClickListener(this);

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
            candy.draw(canvas);
            plus.draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void run() {
        while (true) {
            drawSurface();
            eatCandies();
            if(!handleCollisions()) {
                gameOver();
                //break;
            }
            candy.move();
            bird.move();
            if (plus.isActive()) {
                plus.move();
            }
        }
    }

    public void gameOver(){

    }

    public boolean  handleCollisions(){
        // Check for bird-spike collisions
        boolean isCollide = false;
        if (walls.isLeftWallActive()) {
            for (MovingSpike_left spike : walls.left_spikes) {
                if (bird.collidesWith(spike.getX(), spike.getY(), spike.getWidth(), spike.getHeight())) {
                    handleCollision(); // Handle collision
                    isCollide = true;
                }
            }
        } else {
            for (MovingSpike_right spike : walls.right_spikes) {
                if (bird.collidesWith(spike.getX(), spike.getY(), spike.getWidth(), spike.getHeight())) {
                    handleCollision(); // Handle collision
                    isCollide = true;
                }
            }
        }

        if (bird.getX() <= 0 && walls.isLeftWallActive()) {
            walls.switchWall();
        } else if (bird.getX() >= screenWidth - 144 && !walls.isLeftWallActive()) {
            walls.switchWall();
        }
        return isCollide;
    }
    public void eatCandies() {
        if (bird.collidesWith(candy.getX(), candy.getY(), candy.getWidth(), candy.getHeight())) {
            int candyX = candy.getX();
            int candyY = candy.getY();
            candy.takesCandy();
            candies++;
            score.setText("Score: " + candies);
            plus.activate(candyX + candy.getWidth() / 2 - plus.getBitmapWidth() / 2,
                    candyY + candy.getHeight() / 2 - plus.getBitmapHeight() / 2);
        }
    }




    private void handleCollision() {
        Log.e("GameController", "Collision detected!");
        // Additional logic: Restart the game, reduce health, etc.
    }


    @Override
    public void onClick(View v) {

    }
}
