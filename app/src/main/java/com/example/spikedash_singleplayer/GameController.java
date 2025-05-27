package com.example.spikedash_singleplayer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spikedash_singleplayer.Activities.LeaderboardActivity;
import com.example.spikedash_singleplayer.Activities.StatsActivity;
import com.example.spikedash_singleplayer.Entitys.Bird;
import com.example.spikedash_singleplayer.Entitys.Candy;
import com.example.spikedash_singleplayer.Entitys.CountDown;
import com.example.spikedash_singleplayer.Entitys.Plus;
import com.example.spikedash_singleplayer.Entitys.Spike;
import com.example.spikedash_singleplayer.Entitys.Walls;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class GameController implements Runnable {
    TextView tvScore, tvCandies;
    ImageButton btnPause;
    private Context context;
    private User user;
    private long countdownStartTime;
    private boolean tookCandy = false;
    private Candy candy;
    private Walls walls;
    private Plus plus;
    private CountDown currentNumber;
    private int candies = 0;
    private boolean isRunning = false;
    private boolean isCountingDown = false;
    private long lastCountdownTickTime = 0;
    private int wallScore = 0;
    private GameView gameView;
    private int currentCount = 3;
    private Bird bird;
    private Dialog d;
    public GameController(GameView gameView, Context context, User user,
                          TextView tvScore, TextView tvCandies, ImageButton btnPause) {
        this.gameView = gameView;
        this.context = context;
        this.user = user;
        this.tvScore = tvScore;
        this.tvCandies = tvCandies;
        this.btnPause = btnPause;
    }

    public void initializeGame(Bitmap bitmapBird, Bitmap spikeBitmap, Bitmap candyBitmap, Bitmap plusBitmap) {
        int screenWidth = gameView.getWidth();
        int screenHeight = gameView.getHeight();

        walls = new Walls(screenWidth, screenHeight, spikeBitmap);
        candy = new Candy(screenWidth, screenHeight, candyBitmap);
        plus = new Plus(screenWidth, screenHeight, plusBitmap);
        plus.setX(candy.getX());
        plus.setY(candy.getY());

        gameView.setWalls(walls);
        gameView.setCandy(candy);
        gameView.setPlus(plus);


        btnPause.setOnClickListener(v -> {
            isRunning = false;
            createPauseDialog();
        });


        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .child("difficultyMultiplier")
                .get()
                .addOnSuccessListener(snapshot -> {
                    float multiplier = 1.0f;
                    if (snapshot.exists()) {
                        Float value = snapshot.getValue(Float.class);
                        if (value != null) multiplier = value;
                    }

                    bird = new Bird(screenWidth, screenHeight, bitmapBird, multiplier);
                    gameView.setBird(bird);

                    start();
                });

    }


    public void start() {
        isRunning = true;
        new Thread(this).start();
    }

    public void stop() {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            if (isCountingDown) {
                count();
                continue;
            }
            gameView.drawSurface();
            eatCandies();
            if (!handleCollisions()) {
                isRunning = false;
                continue;
            }

            candy.move();
            bird.move();

            if (plus.isActive()) {
                plus.move();
            }
        }
    }

    public void count() {
        long currentTime = System.currentTimeMillis();

        if (currentNumber != null && currentNumber.isFinished()) {
            currentNumber = null;
        }

        if (currentTime - lastCountdownTickTime >= 1000) {
            currentCount--;
            lastCountdownTickTime = currentTime;

            if (currentCount > 0) {
                currentNumber = new CountDown(gameView.getWidth(), gameView.getHeight(), null, currentCount);
                gameView.setCountDown(currentNumber);
            } else {
                isCountingDown = false;
                currentNumber = null;
            }
        }

        if (currentNumber != null && !currentNumber.isFinished()) {
            currentNumber.move();
            gameView.drawCountdown();
        }
    }


    public void eatCandies() {
        if (bird.collidesWith(candy.getX(), candy.getY(), candy.getWidth(), candy.getHeight())) {
            int candyX = candy.getX();
            int candyY = candy.getY();
            candy.takesCandy();
            SoundManager.play("candy");
            candies++;
            ((AppCompatActivity) context).runOnUiThread(() -> {
                tvCandies.setText(String.valueOf(candies));
            });
            plus.activate(candyX + candy.getWidth() / 2 - plus.getBitmapWidth() / 2,
                    candyY + candy.getHeight() / 2 - plus.getBitmapHeight() / 2);
            tookCandy = false;
        }
        else if (wallScore % 5 == 0 && wallScore != 0) {
            if (!tookCandy) {
                candy.takesCandy();
                tookCandy = true;
            }
        }
        else {
            tookCandy = false;
        }

    }

    public boolean handleCollisions() {
        boolean isCollide = false;
        if (walls.isLeftWallActive()) {
            for (Spike spike : walls.left_spikes) {
                if (bird.collidesWith(spike.getX(), spike.getY(), spike.getWidth(), spike.getHeight())) {
                    handleCollision();
                    isCollide = true;
                }
            }
        } else {
            for (Spike spike : walls.right_spikes) {
                if (bird.collidesWith(spike.getX(), spike.getY(), spike.getWidth(), spike.getHeight())) {
                    handleCollision();
                    isCollide = true;
                }
            }
        }

        if (bird.getX() <= 0 && walls.isLeftWallActive()) {
            walls.switchWall();
            VibrationManager.vibrate(context, 5);
            wallScore++;
            SoundManager.play("select");
            bird.increaseSpeed();
            ((AppCompatActivity) context).runOnUiThread(() -> {
                tvScore.setText("Score: " + wallScore);
            });

        } else if (bird.getX() >= gameView.getWidth() - bird.getWidth() && !walls.isLeftWallActive()) {
            VibrationManager.vibrate(context, 5);
            walls.switchWall();
            wallScore++;
            SoundManager.play("select");
            bird.increaseSpeed();((AppCompatActivity) context).runOnUiThread(() -> {
                tvScore.setText("Score: " + wallScore);
            });

        }
        float floorY = gameView.getHeight() - (gameView.getHeight() / 1920f) * 200;
        if (bird.getY() + bird.getHeight() >= floorY || bird.getY() < (gameView.getHeight() / 1920f) * 250) {
            isCollide = true;
            handleCollision();
        }
        return !isCollide;

    }


    private void handleCollision() {
        SoundManager.play("hit");
        VibrationManager.vibrate(context, 200);
        isRunning = false;
        ((AppCompatActivity) context).runOnUiThread(() -> {
            createGameOverDialog();
        });

    }

    private void createGameOverDialog() {
        d = new Dialog(context);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.over_dialog);

        ImageButton imgLeaderBoard = d.findViewById(R.id.imgLeaderBoard);
        ImageButton imgStats = d.findViewById(R.id.imgStats);
        LinearLayout btnRestart = d.findViewById(R.id.replayButton);
        LinearLayout btnHome = d.findViewById(R.id.homeButton);
        TextView dialogScore = d.findViewById(R.id.tvScore);
        TextView dialogCandies = d.findViewById(R.id.tvCandies);
        dialogScore.setText(String.valueOf(wallScore));
        dialogCandies.setText(String.valueOf(candies));
        TextView tvHighScore = d.findViewById(R.id.tvHighScore);
        TextView tvGames = d.findViewById(R.id.tvGames);


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid());
        user.add(candies);
        userRef.child("balance").setValue(user.getBalance());
        user.addGame();


        if (wallScore >= user.getHighScore()) {
            user.setHighScore(wallScore);
            userRef.child("highScore").setValue(user.getHighScore());
        }

        userRef.child("games").setValue(user.getGames());
        tvGames.setText("GAMES PLAYED: " + String.valueOf(user.getGames()));
        tvHighScore.setText("HIGHSCORE: "+ String.valueOf(user.getHighScore()));

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.play("click");
                d.dismiss();
                Intent intent = new Intent(context, GameActivity.class);
                intent.putExtra("user", user);
                context.startActivity(intent);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.play("click");
                d.dismiss();
                MusicManager.stop();
                MusicManager.release();
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("user", user);
                context.startActivity(intent);
            }
        });

        imgLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.play("click");
                MusicManager.stop();
                MusicManager.release();
                MusicManager.start(context, R.raw.bgm_music);
                d.dismiss();
                Intent intent = new Intent(context, LeaderboardActivity.class);
                intent.putExtra("user", user);
                context.startActivity(intent);
            }
        });

        imgStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.play("click");
                MusicManager.stop();
                MusicManager.release();
                MusicManager.start(context, R.raw.bgm_music);
                d.dismiss();
                Intent intent = new Intent(context, StatsActivity.class);
                intent.putExtra("user", user);
                context.startActivity(intent);
            }
        });
        d.setOnCancelListener(dialog -> {
            d.dismiss();
            MusicManager.stop();
            MusicManager.release();
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("user", user);
            context.startActivity(intent);
        });

        d.show();
    }

    private void createPauseDialog() {
        d = new Dialog(context);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.pause_dialog);
        LinearLayout btnResume = d.findViewById(R.id.btn_resume);
        LinearLayout btnRestart = d.findViewById(R.id.btn_replay);
        LinearLayout btnHome = d.findViewById(R.id.btn_home);

        SeekBar skBgm = d.findViewById(R.id.skBgm);
        SeekBar skSound = d.findViewById(R.id.skSound);
        Switch swVibration = d.findViewById(R.id.swVibration);




        DatabaseReference settingsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .child("settings");

        settingsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot snapshot = task.getResult();
                double sound = snapshot.child("sound").getValue(Double.class) != null ?
                        snapshot.child("sound").getValue(Double.class) : 1.0;
                double bgm = snapshot.child("bgm").getValue(Double.class) != null ?
                        snapshot.child("bgm").getValue(Double.class) : 1.0;
                boolean vibration = snapshot.child("vibration").getValue(Boolean.class) != null &&
                        snapshot.child("vibration").getValue(Boolean.class);

                skSound.setProgress((int) (sound * 100));
                skBgm.setProgress((int) (bgm * 100));
                swVibration.setChecked(vibration);
            }
        });

        skSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float vol = progress / 100f;
                settingsRef.child("sound").setValue(vol);
                SoundManager.setVolume(vol);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        skBgm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float vol = progress / 100f;
                settingsRef.child("bgm").setValue(vol);
                MusicManager.setVolume(vol);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        swVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            VibrationManager.setEnabled(isChecked);

            if (isChecked) {
                VibrationManager.vibrate(context, 25);
            }
            settingsRef.child("vibration").setValue(isChecked);
        });

        btnResume.setOnClickListener(v -> {
            SoundManager.play("click");
            d.dismiss();
            resumeGame();
        });

        btnRestart.setOnClickListener(v -> {
            SoundManager.play("click");
            d.dismiss();
            Intent intent = new Intent(context, GameActivity.class);
            intent.putExtra("user", user);
            context.startActivity(intent);
        });

        btnHome.setOnClickListener(v -> {
            SoundManager.play("click");
            SoundManager.play("click");
            d.dismiss();
            MusicManager.stop();
            MusicManager.release();
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("user", user);
            context.startActivity(intent);
        });
        d.setOnCancelListener(dialog -> {
            d.dismiss();
            resumeGame();
        });

        d.show();


    }

    public float scaleX(float value) {
        return value * (gameView.getWidth() / 1080f);
    }

    public float scaleY(float value) {
        return value * (gameView.getHeight() / 1920f);
    }


    public void resumeGame() {
        isCountingDown = true;
        currentCount = 3;
        countdownStartTime = System.currentTimeMillis();
        lastCountdownTickTime = countdownStartTime;
        currentNumber = new CountDown(gameView.getWidth(), gameView.getHeight(), null, currentCount);
        gameView.setCountDown(currentNumber);

        isRunning = true;
        new Thread(this).start();
    }





}
