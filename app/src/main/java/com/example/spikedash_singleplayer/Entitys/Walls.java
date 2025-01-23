package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import com.example.spikedash_singleplayer.Entitys.Spikes.MovingSpike_left;
import com.example.spikedash_singleplayer.Entitys.Spikes.MovingSpike_right;

import java.util.ArrayList;

public class Walls {

    public ArrayList<MovingSpike_left> left_spikes;
    public ArrayList<MovingSpike_right> right_spikes;
    public boolean touchedRight;
    private int screen_width;
    private int screen_height;
    private Bitmap spikeBitmap;
    private boolean isLeftWallActive;

    public Walls(int ScreenWidth, int ScreenHeight, Bitmap spikeBitmap) {
        left_spikes = new ArrayList<>(10);
        right_spikes = new ArrayList<>(10);
        screen_width = ScreenWidth;
        screen_height = ScreenHeight;
        this.spikeBitmap = spikeBitmap;

        isLeftWallActive = true;
        generateLeft();
    }

    private void generateLeft() {
        left_spikes.clear();
        int numSpikes = 13;
        int spikeSpacing = spikeBitmap.getHeight()+50;
        int starting_point = 50;

        for (int i = 0; i < numSpikes - 2; i++) {
            if (Math.random() < 0.5) {
                left_spikes.add(new MovingSpike_left(-20, starting_point, spikeBitmap)); // Offset slightly for better alignment
            }
            starting_point += spikeSpacing;
        }
    }

    private void generateRight() {
        right_spikes.clear();
        int numSpikes = 13;
        int spikeSpacing = spikeBitmap.getHeight()+50;
        int starting_point = 50;

        for (int i = 0; i < numSpikes; i++) {
            if (Math.random() < 0.5) {
                right_spikes.add(new MovingSpike_right(screen_width - spikeBitmap.getWidth()+20, starting_point, spikeBitmap)); // Align to right wall
            }
            starting_point += spikeSpacing;
        }
    }

    public void draw(Canvas canvas) {
        if (isLeftWallActive) {
            for (MovingSpike_left spike : left_spikes) {
                spike.draw(canvas);
            }
        } else {
            for (MovingSpike_right spike : right_spikes) {
                canvas.save();
                canvas.scale(-1, 1, spike.getX() + spikeBitmap.getWidth() / 2f, 0);
                spike.draw(canvas);
                canvas.restore();
            }
        }
    }


    public void switchWall() {
        if (isLeftWallActive) {
            generateRight();
        } else {
            generateLeft();
        }
        isLeftWallActive = !isLeftWallActive; // Toggle active wall
    }

    public boolean isLeftWallActive() {
        return isLeftWallActive;
    }
}
