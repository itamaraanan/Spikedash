package com.example.spikedash_singleplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnBack;
    TextView gamesTab, highScoreTab;
    View gamesInidcator, highScoreIndicator;
    PlayerStatsAdapter adapter;
    ListView lvPlayerStats;
    ArrayList<PlayerStats> highScoreList = new ArrayList<>();
    ArrayList<PlayerStats> gamesList = new ArrayList<>();
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        btnBack = findViewById(R.id.btnBack);
        gamesTab = findViewById(R.id.gamesTab);
        gamesInidcator = findViewById(R.id.gamesIndicator);
        highScoreIndicator = findViewById(R.id.highScoreIndicator);
        highScoreTab = findViewById(R.id.highScoreTab);
        lvPlayerStats = findViewById(R.id.lvPlayerStats);
        user = getIntent().getParcelableExtra("user");


        btnBack.setOnClickListener(this);
        gamesTab.setOnClickListener(this);
        highScoreTab.setOnClickListener(this);

        setListView();
    }

    private void setListView() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<PlayerStats> allPlayers = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    PlayerStats player = child.getValue(PlayerStats.class);
                    if (player != null) {
                        allPlayers.add(player);
                    }
                }

                // Sort by games
                gamesList = new ArrayList<>(allPlayers);
                Collections.sort(gamesList, (a, b) -> Integer.compare(b.getGames(), a.getGames()));

                // Sort by highScore
                highScoreList = new ArrayList<>(allPlayers);
                Collections.sort(highScoreList, (a, b) -> Integer.compare(b.getHighScore(), a.getHighScore()));

                // Default to games list
                adapter = new PlayerStatsAdapter(LeaderboardActivity.this, 0, 0, gamesList, true,user.getUid());
                lvPlayerStats.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LeaderboardActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        VibrationManager.vibrate(this, 25);
        if (v == gamesTab) {
            gamesTab.setAlpha(1f);
            highScoreTab.setAlpha(0.5f);
            gamesInidcator.setVisibility(View.VISIBLE);
            highScoreIndicator.setVisibility(View.INVISIBLE);

            adapter = new PlayerStatsAdapter(this, 0, 0, gamesList,true, user.getUid());
            lvPlayerStats.setAdapter(adapter);

        } else if (v == highScoreTab) {
            highScoreTab.setAlpha(1f);
            gamesTab.setAlpha(0.5f);
            highScoreIndicator.setVisibility(View.VISIBLE);
            gamesInidcator.setVisibility(View.INVISIBLE);

            adapter = new PlayerStatsAdapter(this, 0, 0, highScoreList, false, user.getUid());
            lvPlayerStats.setAdapter(adapter);

        } else if (v == btnBack) {
            Intent intent = new Intent(LeaderboardActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
