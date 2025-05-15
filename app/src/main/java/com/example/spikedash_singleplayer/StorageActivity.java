package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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

    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.storageContentContainer, new StorageBackFragment())
                .commit();
    }

    @Override
    public void onClick(View v) {
        if (v == skinsTab) {
            skinsTab.setAlpha(1f);
            backgroundsTab.setAlpha(0.5f);
            skinsIndicator.setVisibility(View.VISIBLE);
            backgroundsIndicator.setVisibility(View.INVISIBLE);
            loadFragment(new StorageSkinFragment());
        }

        if (v == backgroundsTab) {
            backgroundsTab.setAlpha(1f);
            skinsTab.setAlpha(0.5f);
            backgroundsIndicator.setVisibility(View.VISIBLE);
            skinsIndicator.setVisibility(View.INVISIBLE);
            loadFragment(new StorageBackFragment());
        }


        if (v == btnBack) {
            finish();
        }
    }
}