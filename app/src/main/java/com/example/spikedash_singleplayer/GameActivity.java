package com.example.spikedash_singleplayer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.spikedash_singleplayer.Activities.LeaderboardActivity;
import com.example.spikedash_singleplayer.Activities.StatsActivity;
import com.example.spikedash_singleplayer.Entitys.Bird;
import com.example.spikedash_singleplayer.Entitys.Candy;
import com.example.spikedash_singleplayer.Entitys.CountDown;
import com.example.spikedash_singleplayer.Entitys.Plus;
import com.example.spikedash_singleplayer.Entitys.Spikes.MovingSpike_left;
import com.example.spikedash_singleplayer.Entitys.Spikes.MovingSpike_right;
import com.example.spikedash_singleplayer.Entitys.Walls;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class GameActivity extends AppCompatActivity {
    GameView gameView;
    FrameLayout frm;
    TextView tvScore, tvCandies;
    ImageButton btnPause;
    User user;
    ImageView backgroundImage;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tvScore = findViewById(R.id.tvScore);
        tvCandies = findViewById(R.id.tvCandies);
        frm = findViewById(R.id.frm);
        btnPause = findViewById(R.id.imbPause);
        backgroundImage = findViewById(R.id.backgroundImage);
        user = getIntent().getParcelableExtra("user");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SettingsManager.applySavedBgmVolume(this, uid);
        SoundManager.init(this);
        loadGameBackground();
        loadEquippedSkin();


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && gameView == null) {
            int w = frm.getWidth();
            int h = frm.getHeight();
            gameView = new GameView(this, w, h, tvScore,tvCandies, btnPause, user);
            frm.addView(gameView);
        }
    }

    private class GameView extends SurfaceView implements Runnable, View.OnClickListener {
        private TextView tvCandies, tvScore;
        private int screenWidth;
        private boolean isRunning;
        private int screenHeight;
        private ImageButton btnPause;
        private Canvas canvas;
        private Bird bird;
        private Candy candy;
        private Plus plus;
        private Walls walls;
        private Bitmap bitmapBird;
        private Bitmap spikeBitmap;
        private Bitmap candyBitmap;
        private Bitmap plusBitmap;
        private Bitmap backgroundBitmap;

        private SurfaceHolder holder;
        private Paint bg;
        private int candies;
        private int wallScore;
        private Thread thread;
        private CountDown currentNumber;
        private boolean isCountingDown;
        private long countdownStartTime;
        private int currentCount;
        private User user;
        private boolean tookCandy;
        Dialog d;

        public GameView(Context context, int screenWidth, int screenHeight, TextView tvScore, TextView tvCandies, ImageButton btnPause,User user) {
            super(context);
            this.candies = 0;
            this.wallScore = 0;
            this.tvScore = tvScore;
            this.tvCandies = tvCandies;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.btnPause = btnPause;
            this.btnPause.setOnClickListener(this);
            this.btnPause.setVisibility(View.INVISIBLE);
            this.user = user;

            initializeGame();

        }

        private float scaleX(float value) {
            return value * (screenWidth / 1080f);
        }

        private float scaleY(float value) {
            return value * (screenHeight / 1920f);
        }
        private float getFloorY() {
            return screenHeight - scaleY(200);
        }



        private void initializeGame() {
            bg = new Paint();
            bg.setARGB(218, 218, 218, 218);

            Bitmap originalBirdBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bird);
            candyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.candy96);
            spikeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.left_spike);
            plusBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
            bitmapBird = Bitmap.createScaledBitmap(originalBirdBitmap, (int) scaleX(144), (int) scaleY(100), false);
            walls = new Walls(screenWidth, screenHeight, spikeBitmap);
            candyBitmap = Bitmap.createScaledBitmap(candyBitmap, (int) scaleX(96), (int) scaleY(96), false);
            candy = new Candy(screenWidth, screenHeight, candyBitmap);
            plusBitmap = Bitmap.createScaledBitmap(plusBitmap, (int) scaleX(600), (int) scaleY(600), false);
            plus = new Plus(screenWidth, screenHeight, plusBitmap);
            plus.setX(candy.getX());
            plus.setY(candy.getY());
            candies = 0;
            tookCandy = false;
            isRunning = true;
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("difficultyMultiplier")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        float multiplier = 1.0f;
                        if (snapshot.exists()) {
                            Float value = snapshot.getValue(Float.class);
                            if (value != null) multiplier = value;
                        }

                        bird = new Bird(screenWidth, screenHeight, bitmapBird, multiplier);
                        holder = getHolder();
                        thread = new Thread(GameView.this);
                        thread.start();
                    });
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!bird.gameSrarted) {
                bird.gameSrarted = true;
                btnPause.setVisibility(View.VISIBLE);
            }
            ((AppCompatActivity) getContext()).runOnUiThread(() -> {
                tvScore.setText("Score: " + wallScore);
            });
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                SoundManager.play("jump");
                bird.jump();
                return true;
            }
            return false;
        }

        private void drawSurface() {
            if (holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();

                if (backgroundBitmap != null) {
                    canvas.drawBitmap(backgroundBitmap, 0, 0, null);
                } else {
                    canvas.drawColor(Color.LTGRAY); // fallback
                }

                bird.draw(canvas);
                walls.draw(canvas);
                candy.draw(canvas);
                plus.draw(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }


        @Override
        public void run() {
            while (isRunning) {
                if (isCountingDown) {
                    count();
                } else {
                    drawSurface();
                    eatCandies();
                    if (!handleCollisions()) {
                        gameOver();
                    }
                    candy.move();
                    bird.move();
                    if (plus.isActive()) {
                        plus.move();
                    }
                }
            }
        }

        private void count() {
            long currentTime = System.currentTimeMillis();

            if (currentNumber != null && currentNumber.isFinished()) {
                currentNumber = null;
            }
            if (currentTime - countdownStartTime > 1000) {
                currentCount--;
                if (currentCount > 0) {
                    currentNumber = new CountDown(screenWidth, screenHeight, null, currentCount);
                    countdownStartTime = currentTime;
                } else {
                    isCountingDown = false;
                }
            }

            if (currentNumber != null && !currentNumber.isFinished()) {
                currentNumber.move();
                drawCountdown();
            }
        }

        private void drawCountdown() {
            if (holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();
                canvas.drawPaint(bg);
                bird.draw(canvas);
                walls.draw(canvas);
                candy.draw(canvas);
                plus.draw(canvas);
                if (currentNumber != null && !currentNumber.isFinished()) {
                    currentNumber.draw(canvas);
                }
                holder.unlockCanvasAndPost(canvas);
            }
        }

        private boolean handleCollisions() {
            boolean isCollide = false;
            if (walls.isLeftWallActive()) {
                for (MovingSpike_left spike : walls.left_spikes) {
                    if (bird.collidesWith(spike.getX(), spike.getY(), spike.getWidth(), spike.getHeight())) {
                        handleCollision();
                        isCollide = true;
                    }
                }
            } else {
                for (MovingSpike_right spike : walls.right_spikes) {
                    if (bird.collidesWith(spike.getX(), spike.getY(), spike.getWidth(), spike.getHeight())) {
                        handleCollision();
                        isCollide = true;
                    }
                }
            }

            if (bird.getX() <= 0 && walls.isLeftWallActive()) {
                walls.switchWall();
                VibrationManager.vibrate(getContext(), 5);
                wallScore++;
                SoundManager.play("select");
                bird.increaseSpeed();
                ((AppCompatActivity) getContext()).runOnUiThread(() -> {
                    tvScore.setText("Score: " + wallScore);
                });
            } else if (bird.getX() >= screenWidth - bird.getWidth() && !walls.isLeftWallActive()) {
                VibrationManager.vibrate(getContext(), 5);
                walls.switchWall();
                wallScore++;
                SoundManager.play("select");
                bird.increaseSpeed();((AppCompatActivity) getContext()).runOnUiThread(() -> {
                    tvScore.setText("Score: " + wallScore);
                });

            }if (bird.getY() + bird.getHeight() >= getFloorY()) {
                bird.setY((int) (getFloorY() - bird.getHeight()));
                isCollide = true;
                handleCollision();
            } else if (bird.getY() < scaleY(250)) {
                isCollide = true;
                handleCollision();
            }



            return !isCollide;
        }

        private void eatCandies() {
            if (bird.collidesWith(candy.getX(), candy.getY(), candy.getWidth(), candy.getHeight())) {
                int candyX = candy.getX();
                int candyY = candy.getY();
                candy.takesCandy();
                SoundManager.play("candy");
                candies++;
                ((AppCompatActivity) getContext()).runOnUiThread(() -> {
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

        private void handleCollision() {
            SoundManager.play("hit");
            VibrationManager.vibrate(getContext(), 200);

            isRunning = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    createGameOverDialog();
                }
            });
        }

        private void createGameOverDialog() {
            d = new Dialog(getContext());
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
                    Intent intent = new Intent(getContext(), GameActivity.class);
                    intent.putExtra("user", user);
                    getContext().startActivity(intent);
                }
            });

            btnHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SoundManager.play("click");
                    d.dismiss();
                    MusicManager.stop();
                    MusicManager.release();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("user", user);
                    getContext().startActivity(intent);
                }
            });

            imgLeaderBoard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SoundManager.play("click");
                    MusicManager.stop();
                    MusicManager.release();
                    MusicManager.start(getContext(), R.raw.bgm_music);
                    d.dismiss();
                    Intent intent = new Intent(getContext(), LeaderboardActivity.class);
                    intent.putExtra("user", user);
                    getContext().startActivity(intent);
                }
            });

            imgStats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SoundManager.play("click");
                    MusicManager.stop();
                    MusicManager.release();
                    MusicManager.start(getContext(), R.raw.bgm_music);
                    d.dismiss();
                    Intent intent = new Intent(getContext(), StatsActivity.class);
                    intent.putExtra("user", user);
                    getContext().startActivity(intent);
                }
            });
            d.setOnCancelListener(dialog -> {
                d.dismiss();
                MusicManager.stop();
                MusicManager.release();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("user", user);
                getContext().startActivity(intent);
            });

            d.show();
        }

        @Override
        public void onClick(View v) {
            if (v == btnPause) {
                isRunning = false;
                createPauseDialog();
            }
        }

        private void createPauseDialog() {
            d = new Dialog(getContext());
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
                    VibrationManager.vibrate(getContext(), 25);
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
                Intent intent = new Intent(getContext(), GameActivity.class);
                intent.putExtra("user", user);
                getContext().startActivity(intent);
            });

            btnHome.setOnClickListener(v -> {
                SoundManager.play("click");
                SoundManager.play("click");
                d.dismiss();
                MusicManager.stop();
                MusicManager.release();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("user", user);
                getContext().startActivity(intent);
            });
            d.setOnCancelListener(dialog -> {
                d.dismiss();
                resumeGame();
            });

            d.show();


        }

        private void resumeGame() {
            isCountingDown = true;
            currentCount = 3;
            currentNumber = new CountDown(screenWidth, screenHeight, null, currentCount);
            countdownStartTime = System.currentTimeMillis();
            isRunning = true;
            thread = new Thread(this);
            thread.start();
        }

        private void gameOver() {
        }
        public void setBackgroundBitmap(Bitmap bitmap) {
            this.backgroundBitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight, true);
        }
        public void setBirdBitmap(Bitmap birdBitmap) {
            this.bitmapBird = birdBitmap;
            this.bird.setBitmap(birdBitmap);
        }


    }

    private void loadGameBackground() {
        if (uid == null || uid.isEmpty()) return;

        FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .child("equippedBackground")
                .get()
                .addOnSuccessListener(snapshot -> {
                    String equippedId = snapshot.getValue(String.class);
                    if (equippedId == null) return;

                    FirebaseFirestore.getInstance().collection("backgrounds")
                            .document(equippedId)
                            .get()
                            .addOnSuccessListener(doc -> {
                                String imageUrl = doc.getString("imageUrl");
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(this)
                                            .asBitmap()
                                            .load(imageUrl)
                                            .into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap resource, com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                                                    if (gameView != null) {
                                                        gameView.setBackgroundBitmap(resource);
                                                    } else {
                                                        GameActivity.this.runOnUiThread(() -> {
                                                            frm.post(() -> {
                                                                if (gameView != null)
                                                                    gameView.setBackgroundBitmap(resource);
                                                            });
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {}
                                            });
                                }
                            });
                });
    }
    private void loadEquippedSkin() {
        if (uid == null || uid.isEmpty()) return;

        FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .child("equippedSkin")
                .get()
                .addOnSuccessListener(snapshot -> {
                    String equippedSkinId = snapshot.getValue(String.class);
                    if (equippedSkinId == null) return;

                    FirebaseFirestore.getInstance().collection("skins")
                            .document(equippedSkinId)
                            .get()
                            .addOnSuccessListener(doc -> {
                                String imageUrl = doc.getString("imageUrl");
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(this)
                                            .asBitmap()
                                            .load(imageUrl)
                                            .into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap resource, com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                                                    Bitmap scaled = Bitmap.createScaledBitmap(resource, 144, 100, false);
                                                    if (gameView != null) {
                                                        gameView.setBirdBitmap(scaled);
                                                    } else {
                                                        GameActivity.this.runOnUiThread(() -> {
                                                            frm.post(() -> {
                                                                if (gameView != null)
                                                                    gameView.setBirdBitmap(scaled);
                                                            });
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {}
                                            });
                                }
                            });
                });
    }




}