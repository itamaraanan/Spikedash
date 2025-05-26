package com.example.spikedash_singleplayer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.spikedash_singleplayer.Activities.DifficultyActivity;
import com.example.spikedash_singleplayer.Activities.FriendsActivity;
import com.example.spikedash_singleplayer.Activities.GiftActivity;
import com.example.spikedash_singleplayer.Activities.LeaderboardActivity;
import com.example.spikedash_singleplayer.Activities.ProfileActivity;
import com.example.spikedash_singleplayer.Activities.SettingsActivity;
import com.example.spikedash_singleplayer.Activities.ShopActivity;
import com.example.spikedash_singleplayer.Activities.StatsActivity;
import com.example.spikedash_singleplayer.Activities.StorageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout btnStart;
    private ImageButton btnLeaderBoard, btnDifficulty,btnProfile, btnGift,
            btnSettings, btnStats, btnShop, btnInventory,btnFriends;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference ref = db.getReference("users");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private User currentUser;
    private ImageView backgroundImage, birdImage;
    private String uid;
    private ActivityResultLauncher<Intent> gameLauncher;
    private boolean isAccountFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnShop = findViewById(R.id.btnShop);
        btnLeaderBoard = findViewById(R.id.btnLeaderboard);
        btnProfile = findViewById(R.id.btnProfile);
        btnStats = findViewById(R.id.btnStats);
        btnStart = findViewById(R.id.btnStart);
        btnDifficulty = findViewById(R.id.btnDifficulty);
        btnGift = findViewById(R.id.btnGift);
        btnSettings = findViewById(R.id.btnSettings);
        btnFriends = findViewById(R.id.btnFriends);
        btnInventory = findViewById(R.id.btnInventory);
        backgroundImage = findViewById(R.id.backgroundImage);
        birdImage = findViewById(R.id.birdImage);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        SettingsManager.applySavedBgmVolume(this, uid);
        SoundManager.init(this);
        MusicManager.start(this, R.raw.bgm_music);
        VibrationManager.syncWithFirebase();


        btnLeaderBoard.setOnClickListener(this);
        btnShop.setOnClickListener(this);
        btnStats.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        btnDifficulty.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnGift.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnInventory.setOnClickListener(this);
        btnFriends.setOnClickListener(this);

        gameLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {}
                }
        );

        currentUser();
        loadImage("equippedBackground", "backgrounds", backgroundImage, "BackgroundDebug");
        loadImage("equippedSkin", "skins", birdImage, "SkinDebug");

    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finishAffinity();
                })
                .setNegativeButton("No", null)
                .show();
    }



    public void onClick(View v) {
        VibrationManager.vibrate(this, 25);

        if (v == btnStart) {
            handleClick(GameActivity.class, true, R.raw.game_music, true);
            return;
        }

        SoundManager.play("click");

         if (v == btnDifficulty) {
            handleClick(DifficultyActivity.class, true);
        } else if (v == btnGift) {
            handleClick(GiftActivity.class, true);
        } else if (v == btnSettings) {
            handleClick(SettingsActivity.class, false);
        } else if (v == btnStats) {
            handleClick(StatsActivity.class, true);
        } else if (v == btnShop) {
            handleClick(ShopActivity.class, true);
        } else if (v == btnProfile) {
            handleClick(ProfileActivity.class, true);
        } else if (v == btnLeaderBoard) {
            handleClick(LeaderboardActivity.class, true);
        } else if (v == btnInventory) {
            handleClick(StorageActivity.class, true);
        } else if (v == btnFriends) {
            handleClick(FriendsActivity.class, true);
        }
    }

    private void handleClick(Class<?> activityClass, boolean needsUser) {
        handleClick(activityClass, needsUser, -1, false);
    }

    private void handleClick(Class<?> activityClass, boolean needsUser, int bgmResId, boolean stopAndStartMusic) {
        if (needsUser && currentUser == null) {
            Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
            return;
        }

        if (stopAndStartMusic) {
            MusicManager.stop();
            MusicManager.release();
            MusicManager.start(this, bgmResId);
        }

        Intent intent = new Intent(MainActivity.this, activityClass);
        if (needsUser) {
            intent.putExtra("user", currentUser);
        }
        gameLauncher.launch(intent);
    }

    private void loadImage(String userField, String firestoreCollection, ImageView targetView, String logTag) {
        FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .child(userField)
                .get()
                .addOnSuccessListener(snapshot -> {
                    String equippedId = snapshot.getValue(String.class);
                    Log.d(logTag, "Equipped ID: " + equippedId);
                    if (equippedId == null) return;

                    FirebaseFirestore.getInstance().collection(firestoreCollection)
                            .document(equippedId)
                            .get()
                            .addOnSuccessListener(doc -> {
                                String imageUrl = doc.getString("imageUrl");
                                Log.d(logTag, "Image URL: " + imageUrl);
                                if (imageUrl != null) {
                                    Glide.with(this).load(imageUrl).into(targetView);
                                }
                            });
                });
    }



    public void currentUser() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            isAccountFound = false;
            return;
        }
        currentUser = new User();
        currentUser.setUid(uid);
        currentUser.setEmail(firebaseUser.getEmail());

        DatabaseReference userRef = ref.child(uid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User dbUser = dataSnapshot.getValue(User.class);
                    if (dbUser != null) {
                        currentUser = dbUser;
                        isAccountFound = true;
                    }
                } else {

                    String username = firebaseUser.getDisplayName();
                    if (username == null || username.isEmpty()) {

                        String email = firebaseUser.getEmail();
                        username = email != null ? email.split("@")[0] : "User" + uid.substring(0, 5);
                    }

                    currentUser.setUsername(username);
                    userRef.setValue(currentUser);
                    isAccountFound = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Database error: " + databaseError.getMessage());
            }
        });
    }
}