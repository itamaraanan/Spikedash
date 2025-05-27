package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Canvas;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;

public class Walls {

    public ArrayList<Spike> left_spikes;
    public ArrayList<Spike> right_spikes;
    private int screen_width;
    private int screen_height;
    private Bitmap spikeBitmap;
    private boolean isLeftWallActive;

    public Walls(int ScreenWidth, int ScreenHeight, Bitmap spikeBitmap) {
        // Initialize the walls with the screen dimensions and spike bitmap
        this.screen_width = ScreenWidth;
        this.screen_height = ScreenHeight;
        this.spikeBitmap = spikeBitmap;

        left_spikes = new ArrayList<>();
        right_spikes = new ArrayList<>();

        isLeftWallActive = true;
        generateLeft();
    }

    private int scaleY(int value) {
        return (int) (value * (screen_height / 1920f));
    }

    private int scaleX(int value) {
        return (int) (value * (screen_width / 1080f));
    }

    private void generateLeft() {
        // Clear the left spikes list before generating new ones
        left_spikes.clear();
        // Calculate the spacing and number of spikes based on the screen height
        int spikeSpacing = spikeBitmap.getHeight() + scaleY(60);
        int availableSpots = (screen_height - scaleY(100)) / spikeSpacing;
        int maxSpikes = Math.max(1, availableSpots / 2);
        // Create a list of candidate spots to place spikes
        ArrayList<Integer> candidateSpots = new ArrayList<>();
        for (int i = 0; i < availableSpots; i++) {
            candidateSpots.add(i);
        }
        // Shuffle the candidate spots to randomize spike placement
        Collections.shuffle(candidateSpots);
        // Generate spikes for the left wall
        for (int i = 0; i < maxSpikes; i++) {
            int index = candidateSpots.get(i);
            int y = scaleY(50) + index * spikeSpacing;
            left_spikes.add(new Spike(scaleX(-20), y, spikeBitmap));
        }
    }

    private void generateRight() {
        // Clear the right spikes list before generating new ones
        right_spikes.clear();
        // Calculate the spacing and number of spikes based on the screen height
        int spikeSpacing = spikeBitmap.getHeight() + scaleY(50);
        int availableSpots = (screen_height - scaleY(100)) / spikeSpacing;
        int maxSpikes = Math.max(1, availableSpots / 2);
        // Create a list of candidate spots to place spikes
        ArrayList<Integer> candidateSpots = new ArrayList<>();
        for (int i = 0; i < availableSpots; i++) {
            candidateSpots.add(i);
        }
        // Shuffle the candidate spots to randomize spike placement
        Collections.shuffle(candidateSpots);
        // Generate spikes for the right wall
        for (int i = 0; i < maxSpikes; i++) {
            int index = candidateSpots.get(i);
            int y = scaleY(50) + index * spikeSpacing;
            right_spikes.add(new Spike(screen_width - spikeBitmap.getWidth() - scaleX(-20), y, spikeBitmap));
        }
    }

    public void draw(Canvas canvas) {
        // Draw the spikes on the active wall
        if (isLeftWallActive) {
            for (Spike spike : left_spikes) {
                spike.draw(canvas);
            }
        } else {
            for (Spike spike : right_spikes) {
                //flip the spike horizontally
                canvas.save();
                canvas.scale(-1, 1, spike.getX() + spikeBitmap.getWidth() / 2f, 0);
                spike.draw(canvas);
                canvas.restore();
            }
        }
    }

    public void switchWall() {
        // Switch between left and right walls
        if (isLeftWallActive) {
            generateRight();
        } else {
            generateLeft();
        }
        isLeftWallActive = !isLeftWallActive;
    }

    public boolean isLeftWallActive() {
        return isLeftWallActive;
    }
}