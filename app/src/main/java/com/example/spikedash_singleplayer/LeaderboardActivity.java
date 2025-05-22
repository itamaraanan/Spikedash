package com.example.spikedash_singleplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnBack;
    TextView friendsTab, globalTab;
    View gamesInidcator, globalIndicator;
    GlobalLeaderAdapter adapter;
    ListView lvPlayerStats;
    ArrayList<PlayerStats> globalList = new ArrayList<>();
    ArrayList<PlayerStats> gamesList = new ArrayList<>();
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        btnBack = findViewById(R.id.btnBack);
        friendsTab = findViewById(R.id.friendsTab);
        gamesInidcator = findViewById(R.id.friendsIndicator);
        globalIndicator = findViewById(R.id.globalIndicator);
        globalTab = findViewById(R.id.globalTab);

        btnBack.setOnClickListener(this);
        friendsTab.setOnClickListener(this);
        globalTab.setOnClickListener(this);
        //loading fragment to start
        loadFragment(new GlobalLeaderFragment());

    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentContainer, fragment)
                .commit();

    }

    @Override
    public void onClick(View v) {
        VibrationManager.vibrate(this, 25);
        SoundManager.play("click");

        if (v == friendsTab) {
            friendsTab.setAlpha(1f);  
            globalTab.setAlpha(0.5f);
            gamesInidcator.setVisibility(View.VISIBLE);
            globalIndicator.setVisibility(View.INVISIBLE);
            loadFragment(new FriendsLeaderFragment());

        } else if (v == globalTab) {
            globalTab.setAlpha(1f);
            friendsTab.setAlpha(0.5f);
            globalIndicator.setVisibility(View.VISIBLE);
            gamesInidcator.setVisibility(View.INVISIBLE);
            loadFragment(new GlobalLeaderFragment());
        } else if (v == btnBack) {
            finish();
        }
    }
}
