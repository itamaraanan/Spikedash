package com.example.spikedash_singleplayer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.spikedash_singleplayer.Fragments.FriendsLeaderFragment;
import com.example.spikedash_singleplayer.Fragments.GlobalLeaderFragment;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.Managers.SoundManager;
import com.example.spikedash_singleplayer.Managers.VibrationManager;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnBack;
    TextView friendsTab, globalTab;
    View gamesInidcator, globalIndicator;

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
        // Load the specified fragment into the container
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
            // Switch to friends tab
            friendsTab.setAlpha(1f);  
            globalTab.setAlpha(0.5f);
            gamesInidcator.setVisibility(View.VISIBLE);
            globalIndicator.setVisibility(View.INVISIBLE);
            loadFragment(new FriendsLeaderFragment());

        } else if (v == globalTab) {
            // Switch to global tab
            globalTab.setAlpha(1f);
            friendsTab.setAlpha(0.5f);
            globalIndicator.setVisibility(View.VISIBLE);
            gamesInidcator.setVisibility(View.INVISIBLE);
            loadFragment(new GlobalLeaderFragment());
        } else if (v == btnBack) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
