package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import com.example.spikedash_singleplayer.Entitys.Spikes.MovingSpike_left;
import com.example.spikedash_singleplayer.Entitys.Spikes.MovingSpike_right;

import java.util.ArrayList;

public class Walls {

    public ArrayList<MovingSpike_left> left_spikes;
    public ArrayList<MovingSpike_right> right_spikes;

    private int screen_width;
    private int screen_height;
    private Bitmap spikeBitmap;
    private boolean isLeftWallActive; // True if the left wall is active

    public Walls(int ScreenWidth, int ScreenHeight, Bitmap spikeBitmap) {
        left_spikes = new ArrayList<>(10);
        right_spikes = new ArrayList<>(10);
        screen_width = ScreenWidth;
        screen_height = ScreenHeight;
        this.spikeBitmap = spikeBitmap;

        isLeftWallActive = false; // Start with the right wall active
        generateRight(); // Generate the initial spikes on the right wall
    }

    private void generateLeft() {
        left_spikes.clear();
        int numSpikes = 7;
        int spikeSpacing = screen_height / numSpikes; // Evenly space spikes
        int starting_point = 50;

        for (int i = 0; i < numSpikes - 2; i++) { // 8 spikes for left wall
            if (Math.random() < 0.5) { // 50% chance to generate a spike
                left_spikes.add(new MovingSpike_left(-20, starting_point, spikeBitmap)); // Offset slightly for better alignment
            }
            starting_point += spikeSpacing;
        }
    }

    private void generateRight() {
        right_spikes.clear();
        int numSpikes = 7;
        int spikeSpacing = screen_height / numSpikes;
        int starting_point = 50;

        for (int i = 0; i < numSpikes; i++) { // 10 spikes for right wall
            if (Math.random() < 0.5) { // 50% chance to generate a spike
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
                canvas.save(); // Save the current state of the canvas
                // Flip horizontally by scaling x by -1 and translating to compensate for the flip
                canvas.scale(-1, 1, spike.getX() + spikeBitmap.getWidth() / 2f, 0);
                spike.draw(canvas);
                canvas.restore(); // Restore the original state of the canvas
            }
        }
    }


    public void switchWall() {
        if (isLeftWallActive) {
            // Switch to the right wall
            generateRight();
        } else {
            // Switch to the left wall
            generateLeft();
        }
        isLeftWallActive = !isLeftWallActive; // Toggle active wall
    }

    public boolean isLeftWallActive() {
        return isLeftWallActive;
    }
}
