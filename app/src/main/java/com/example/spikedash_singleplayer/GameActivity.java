package com.example.spikedash_singleplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;

import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class GameActivity extends AppCompatActivity {
    FrameLayout frm;
    GameView gameView;
    GameController gameController;
    TextView tvScore, tvCandies;
    ImageButton btnPause;
    ImageView backgroundImage;
    User user;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        frm = findViewById(R.id.frm);
        tvScore = findViewById(R.id.tvScore);
        tvCandies = findViewById(R.id.tvCandies);
        btnPause = findViewById(R.id.imbPause);
        backgroundImage = findViewById(R.id.backgroundImage);

        user = getIntent().getParcelableExtra("user");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SoundManager.init(this);
        SettingsManager.applySavedBgmVolume(this, uid);


        gameView = new GameView(this);
        frm.addView(gameView);

        gameController = new GameController(gameView, this, user, tvScore, tvCandies, btnPause);
        gameView.setGameController(gameController);


        loadGameBackground();
        loadEquippedSkin();
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
                                                public void onResourceReady(Bitmap resource, com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                                                    Bitmap scaledBird = Bitmap.createScaledBitmap(resource, 144, 100, false);

                                                    Bitmap candyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_candyy);
                                                    candyBitmap = Bitmap.createScaledBitmap(candyBitmap, (int) gameController.scaleX(96), (int)  gameController.scaleY(96), false);

                                                    Bitmap spikeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.left_spike);

                                                    Bitmap plusBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
                                                    plusBitmap = Bitmap.createScaledBitmap(plusBitmap, (int) gameController.scaleX(600), (int) gameController.scaleY(600), false);

                                                    gameController.initializeGame(scaledBird, spikeBitmap, candyBitmap, plusBitmap);
                                                }


                                                @Override
                                                public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {}
                                            });
                                }
                            });
                });
    }




}