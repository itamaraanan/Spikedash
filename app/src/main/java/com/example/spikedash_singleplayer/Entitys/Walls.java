package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.spikedash_singleplayer.R;
import java.util.ArrayList;
import com.example.spikedash_singleplayer.Entitys.Spikes.MovingSpike_left;
import com.example.spikedash_singleplayer.Entitys.Spikes.MovingSpike_right;

public class Walls {

    public ArrayList<MovingSpike_left> left_spikes;
    public ArrayList<MovingSpike_right> right_spikes;

    private int screen_width;
    private int screen_height;
    private Bitmap spikeBitmap;

    public Walls(int ScreenWidth, int ScreenHeight, Bitmap spikeBitmap) {
        left_spikes = new ArrayList<>(10);
        right_spikes = new ArrayList<>(10);
        screen_width = ScreenWidth;
        screen_height = ScreenHeight;
        this.spikeBitmap = spikeBitmap;
        generateLeft();
        generateRight();
    }

    private void generateLeft(){
        int starting_point = 0;
        for (int i = 0; i < 10; i++) {
            int random1 = (int) (Math.random() * 3);
            int random2 = (int) (Math.random() * 3);
            if (random1 == random2) {
                left_spikes.add(new MovingSpike_left(50, starting_point, spikeBitmap));
            }
            starting_point -= 75;
        }
    }

    private void generateRight(){
        int starting_point = screen_height - 100;
        for (int i = 0; i < 10; i++) {
            int random1 = (int) (Math.random() * 3);
            int random2 = (int) (Math.random() * 3);
            if (random1 == random2) {
                right_spikes.add(new MovingSpike_right(screen_width - 50, starting_point, spikeBitmap));
            }
            starting_point -= 100;
        }
    }

    public void draw(Canvas canvas){
        for (MovingSpike_left spike : left_spikes) {
            spike.draw(canvas);
        }
        for (MovingSpike_right spike : right_spikes) {
            spike.draw(canvas);
        }
    }

    public void switchLeft(){
        left_spikes.clear();
        generateLeft();
    }

    public void switchRight(){
        right_spikes.clear();
        generateRight();
    }
}