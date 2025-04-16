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
    TextView winsTab, highScoreTab;
    View winsInidcator, highScoreIndicator;
    PlayerStatsAdapter adapter;
    ListView lvPlayerStats;
    ArrayList<PlayerStats> highScoreList = new ArrayList<>();
    ArrayList<PlayerStats> winsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        btnBack = findViewById(R.id.btnBack);
        winsTab = findViewById(R.id.winsTab);
        winsInidcator = findViewById(R.id.winsIndicator);
        highScoreIndicator = findViewById(R.id.highScoreIndicator);
        highScoreTab = findViewById(R.id.highScoreTab);
        lvPlayerStats = findViewById(R.id.lvPlayerStats);

        btnBack.setOnClickListener(this);
        winsTab.setOnClickListener(this);
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

                // Sort by wins
                winsList = new ArrayList<>(allPlayers);
                Collections.sort(winsList, (a, b) -> Integer.compare(b.getWins(), a.getWins()));

                // Sort by highScore
                highScoreList = new ArrayList<>(allPlayers);
                Collections.sort(highScoreList, (a, b) -> Integer.compare(b.getHighScore(), a.getHighScore()));

                // Default to wins list
                adapter = new PlayerStatsAdapter(LeaderboardActivity.this, 0, 0, winsList, true );
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
        if (v == winsTab) {
            winsTab.setAlpha(1f);
            highScoreTab.setAlpha(0.5f);
            winsInidcator.setVisibility(View.VISIBLE);
            highScoreIndicator.setVisibility(View.INVISIBLE);

            adapter = new PlayerStatsAdapter(this, 0, 0, winsList,true);
            lvPlayerStats.setAdapter(adapter);

        } else if (v == highScoreTab) {
            highScoreTab.setAlpha(1f);
            winsTab.setAlpha(0.5f);
            highScoreIndicator.setVisibility(View.VISIBLE);
            winsInidcator.setVisibility(View.INVISIBLE);

            adapter = new PlayerStatsAdapter(this, 0, 0, highScoreList, false);
            lvPlayerStats.setAdapter(adapter);

        } else if (v == btnBack) {
            Intent intent = new Intent(LeaderboardActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
