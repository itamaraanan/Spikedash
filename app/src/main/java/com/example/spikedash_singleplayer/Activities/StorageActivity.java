package com.example.spikedash_singleplayer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.spikedash_singleplayer.Fragments.StorageBackFragment;
import com.example.spikedash_singleplayer.Fragments.StorageSkinFragment;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.SoundManager;
import com.example.spikedash_singleplayer.User;
import com.example.spikedash_singleplayer.VibrationManager;

public class StorageActivity extends AppCompatActivity implements View.OnClickListener {

    TextView skinsTab, backgroundsTab;
    View skinsIndicator, backgroundsIndicator;
    User user;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        skinsTab = findViewById(R.id.skinsTab);
        backgroundsTab = findViewById(R.id.backgroundsTab);
        skinsIndicator = findViewById(R.id.skinsIndicator);
        backgroundsIndicator = findViewById(R.id.backgroundsIndicator);
        backgroundsTab.setAlpha(0.5f);
        btnBack = findViewById(R.id.btnBack);
        user = getIntent().getParcelableExtra("user");



        skinsTab.setOnClickListener(this);
        backgroundsTab.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        loadFragment(new StorageSkinFragment());

    }
    private void loadFragment(Fragment fragment) {
        // Load the specified fragment into the container
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.storageContentContainer, fragment)
                .commit();
    }

    @Override
    public void onClick(View v) {
        VibrationManager.vibrate(this, 25);
        SoundManager.play("click");

        if (v == skinsTab) {
            // Switch to skins tab
            skinsTab.setAlpha(1f);
            backgroundsTab.setAlpha(0.5f);
            skinsIndicator.setVisibility(View.VISIBLE);
            backgroundsIndicator.setVisibility(View.INVISIBLE);
            loadFragment(new StorageSkinFragment());
        }

        if (v == backgroundsTab) {
            // Switch to backgrounds tab
            backgroundsTab.setAlpha(1f);
            skinsTab.setAlpha(0.5f);
            backgroundsIndicator.setVisibility(View.VISIBLE);
            skinsIndicator.setVisibility(View.INVISIBLE);
            loadFragment(new StorageBackFragment());
        }

        if (v == btnBack) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("user", user); // החזר את המשתמש
            setResult(RESULT_OK, resultIntent);
            finish();
        }

    }
}