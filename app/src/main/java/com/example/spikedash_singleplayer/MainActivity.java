package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout btnStart;
    private ImageButton btnLeaderBoard, btnDifficulty,btnProfile, btnGift, btnSettings, btnStats, btnShop;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference ref = db.getReference("users");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private User currentUser;
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

        btnLeaderBoard.setOnClickListener(this);
        btnShop.setOnClickListener(this);
        btnStats.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        btnDifficulty.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnGift.setOnClickListener(this);
        btnSettings.setOnClickListener(this);

        currentUser();

    }

    @Override
    public void onClick(View v) {
        if(v == btnStart) {
            if (currentUser != null) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
            }
        }
        if(v == btnDifficulty){
            Intent intent = new Intent(MainActivity.this, DifficultyActivity.class);
            startActivity(intent);
        }
        if(v == btnGift){
            if (currentUser != null) {
                Intent intent = new Intent(MainActivity.this, GiftActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
            }
        }
        if(v == btnSettings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if(v == btnStats){
             if (currentUser != null) {
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
            }
        }
        if(v == btnShop){
            if (currentUser != null) {
                Intent intent = new Intent(MainActivity.this, ShopActicity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
            }
        }
        if(v == btnProfile){
            if (currentUser != null) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
            }

        }
        if (v == btnLeaderBoard){
            if (currentUser != null) {
                Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void currentUser() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            isAccountFound = false;
            return;
        }

        String uid = firebaseUser.getUid();
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