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
            btnSettings, btnStats, btnShop, btnInventory,btnTrade,btnFriends;
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
        btnTrade = findViewById(R.id.btnTrade);
        backgroundImage = findViewById(R.id.backgroundImage);
        birdImage = findViewById(R.id.birdImage);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        btnTrade.setOnClickListener(this);

        gameLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                    }
                }
        );


        currentUser();
        //loadBackground();
        loadSkin();
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finishAffinity();
                    System.exit(0);
                })
                .setNegativeButton("No", null)
                .show();
    }



    @Override
    public void onClick(View v) {
        VibrationManager.vibrate(this, 25);
        if(v == btnStart) {
            if (currentUser != null) {
                MusicManager.stop();
                MusicManager.release();
                MusicManager.start(this, R.raw.game_music);
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("user", currentUser);
                SoundManager.play("start");
                gameLauncher.launch(intent);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            SoundManager.play("click");
            if(v == btnDifficulty){
                if (currentUser != null) {
                    Intent intent = new Intent(MainActivity.this, DifficultyActivity.class);
                    intent.putExtra("user", currentUser);
                    gameLauncher.launch(intent);
                } else {
                    Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
                }
            }
            if(v == btnGift){
                if (currentUser != null) {
                    Intent intent = new Intent(MainActivity.this, GiftActivity.class);
                    intent.putExtra("user", currentUser);
                    gameLauncher.launch(intent);
                } else {
                    Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
                }
            }
            if(v == btnSettings){
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                gameLauncher.launch(intent);
            }
            if(v == btnStats){
                 if (currentUser != null) {
                    Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                    intent.putExtra("user", currentUser);
                    gameLauncher.launch(intent);
                } else {
                    Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
                }
            }
            if(v == btnShop){
                if (currentUser != null) {
                    Intent intent = new Intent(MainActivity.this, ShopActicity.class);
                    intent.putExtra("user", currentUser);
                    gameLauncher.launch(intent);
                } else {
                    Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
                }
            }
            if(v == btnProfile){
                if (currentUser != null) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("user", currentUser);
                    gameLauncher.launch(intent);
                } else {
                    Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
                }

            }
            if (v == btnLeaderBoard){
                if (currentUser != null) {
                    Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
                    intent.putExtra("user", currentUser);
                    gameLauncher.launch(intent);
                } else {
                    Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
                }
            }
            if (v == btnInventory){
                if (currentUser != null) {
                    Intent intent = new Intent(MainActivity.this, StorageActivity.class);
                    intent.putExtra("user", currentUser);
                    gameLauncher.launch(intent);
                } else {
                    Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
                }
            }
            if (v == btnFriends){
                if (currentUser != null) {
                    Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                    intent.putExtra("user", currentUser);
                    gameLauncher.launch(intent);
                } else {
                    Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
                }
            }
            if (v == btnTrade){
                if (currentUser != null) {
                    Intent intent = new Intent(MainActivity.this, TradeActivity.class);
                    intent.putExtra("user", currentUser);
                    gameLauncher.launch(intent);
                } else {
                    Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
    public void loadBackground() {
        FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .child("equippedBackground")
                .get()
                .addOnSuccessListener(snapshot -> {
                    String equippedId = snapshot.getValue(String.class);
                    Log.d("BackgroundDebug", "Equipped ID: " + equippedId);

                    if (equippedId == null) return;

                    FirebaseFirestore.getInstance().collection("backgrounds")
                            .document(equippedId)
                            .get()
                            .addOnSuccessListener(doc -> {
                                String imageUrl = doc.getString("imageUrl");
                                Log.d("BackgroundDebug", "Image URL: " + imageUrl);

                                if (imageUrl != null) {
                                    Glide.with(this)
                                            .load(imageUrl)
                                            .into(backgroundImage);
                                }
                            });
                });
    }


    public void loadSkin() {
        FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .child("equippedSkin")
                .get()
                .addOnSuccessListener(snapshot -> {
                    String equippedId = snapshot.getValue(String.class);
                    Log.d("SkinDebug", "Equipped ID: " + equippedId);

                    if (equippedId == null) return;

                    FirebaseFirestore.getInstance().collection("skins")
                            .document(equippedId)
                            .get()
                            .addOnSuccessListener(doc -> {
                                String imageUrl = doc.getString("imageUrl");
                                Log.d("SkinDebug", "Image URL: " + imageUrl);

                                if (imageUrl != null) {
                                    Glide.with(this)
                                            .load(imageUrl)
                                            .into(birdImage);
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