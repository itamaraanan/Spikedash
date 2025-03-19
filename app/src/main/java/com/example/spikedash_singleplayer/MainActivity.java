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
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        }
        if(v == btnDifficulty){
            Intent intent = new Intent(MainActivity.this, DifficultyActivity.class);
            startActivity(intent);
        }
        if(v == btnGift){
            Intent intent = new Intent(MainActivity.this, GiftActivity.class);
            startActivity(intent);
        }
        if(v == btnSettings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if(v == btnStats){
            Intent intent = new Intent(MainActivity.this, StatsActivity.class);
            startActivity(intent);
        }
        if(v == btnShop){
            Intent intent = new Intent(MainActivity.this, ShopActicity.class);
            startActivity(intent);
        }
        if(v == btnProfile){
            if (currentUser != null) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("email", currentUser.email);
                intent.putExtra("username", currentUser.username);
                startActivity(intent);
            } else {
                // Handle the case when user data isn't loaded yet
                // Maybe show a loading indicator or toast message
                Toast.makeText(this, "Loading user data, please try again", Toast.LENGTH_SHORT).show();
            }

        }
        if (v == btnLeaderBoard){
            Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
            startActivity(intent);
        }

    }

    public void currentUser() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            isAccountFound = false;
            return;
        }

        String uid = firebaseUser.getUid();

        // If database doesn't allow access, at least get basic info from FirebaseUser
        currentUser = new User();
        currentUser.setUid(uid);
        currentUser.setEmail(firebaseUser.getEmail());

        // Then try to get full details from database
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
                    // Create a new user record if it doesn't exist
                    if (currentUser.getUsername() == null) {
                        currentUser.setUsername(firebaseUser.getDisplayName() != null ?
                                firebaseUser.getDisplayName() : "User");
                        userRef.setValue(currentUser);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log error instead of throwing exception
                Log.e("Firebase", "Database error: " + databaseError.getMessage());
                // Continue with basic user info from FirebaseUser
            }
        });
    }
}