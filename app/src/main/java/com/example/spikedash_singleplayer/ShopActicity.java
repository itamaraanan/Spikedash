package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ShopActicity extends AppCompatActivity implements View.OnClickListener {
    TextView skinsTab, backgroundsTab;
    View skinsIndicator, backgroundsIndicator;
    ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        skinsTab = findViewById(R.id.skinsTab);
        backgroundsTab = findViewById(R.id.backgroundsTab);
        skinsIndicator = findViewById(R.id.skinsIndicator);
        backgroundsIndicator = findViewById(R.id.backgroundsIndicator);
        btnBack = findViewById(R.id.btnBack);

        skinsTab.setOnClickListener(this);
        backgroundsTab.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        loadFragment(new SkinsFragment());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentContainer, fragment)
                .commit();
    }

    @Override
    public void onClick(View v) {
        if (v == skinsTab) {
            skinsTab.setAlpha(1f);
            backgroundsTab.setAlpha(0.5f);
            skinsIndicator.setVisibility(View.VISIBLE);
            backgroundsIndicator.setVisibility(View.INVISIBLE);
            loadFragment(new SkinsFragment());
        }

        if (v == backgroundsTab) {
            backgroundsTab.setAlpha(1f);
            skinsTab.setAlpha(0.5f);
            backgroundsIndicator.setVisibility(View.VISIBLE);
            skinsIndicator.setVisibility(View.INVISIBLE);
            loadFragment(new BackgroundsFragment());
        }

        if (v == btnBack) {
            startActivity(new Intent(ShopActicity.this, MainActivity.class));
        }
    }

}