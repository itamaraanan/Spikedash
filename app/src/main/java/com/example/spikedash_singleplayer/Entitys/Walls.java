package com.example.spikedash_singleplayer.Entitys;

import android.graphics.Canvas;
import android.graphics.Bitmap;

import com.example.spikedash_singleplayer.Entitys.Spikes.MovingSpike_left;
import com.example.spikedash_singleplayer.Entitys.Spikes.MovingSpike_right;

import java.util.ArrayList;
import java.util.Collections;

public class Walls {

    public ArrayList<MovingSpike_left> left_spikes;
    public ArrayList<MovingSpike_right> right_spikes;
    private int screen_width;
    private int screen_height;
    private Bitmap spikeBitmap;
    private boolean isLeftWallActive;

    public Walls(int ScreenWidth, int ScreenHeight, Bitmap spikeBitmap) {
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
        left_spikes.clear();
        int spikeSpacing = spikeBitmap.getHeight() + scaleY(60);
        int availableSpots = (screen_height - scaleY(100)) / spikeSpacing;
        int maxSpikes = Math.max(1, availableSpots / 2);

        ArrayList<Integer> candidateSpots = new ArrayList<>();
        for (int i = 0; i < availableSpots; i++) {
            candidateSpots.add(i);
        }
        Collections.shuffle(candidateSpots);

        for (int i = 0; i < maxSpikes; i++) {
            int index = candidateSpots.get(i);
            int y = scaleY(50) + index * spikeSpacing;
            left_spikes.add(new MovingSpike_left(scaleX(-20), y, spikeBitmap));
        }
    }

    private void generateRight() {
        right_spikes.clear();
        int spikeSpacing = spikeBitmap.getHeight() + scaleY(50);
        int availableSpots = (screen_height - scaleY(100)) / spikeSpacing;
        int maxSpikes = Math.max(1, availableSpots / 2);

        ArrayList<Integer> candidateSpots = new ArrayList<>();
        for (int i = 0; i < availableSpots; i++) {
            candidateSpots.add(i);
        }
        Collections.shuffle(candidateSpots);

        for (int i = 0; i < maxSpikes; i++) {
            int index = candidateSpots.get(i);
            int y = scaleY(50) + index * spikeSpacing;
            right_spikes.add(new MovingSpike_right(screen_width - spikeBitmap.getWidth() - scaleX(-20), y, spikeBitmap));
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
        isLeftWallActive = !isLeftWallActive;
    }

    public boolean isLeftWallActive() {
        return isLeftWallActive;
    }
}