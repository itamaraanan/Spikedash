package com.example.spikedash_singleplayer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.spikedash_singleplayer.Fragments.BackgroundsFragment;
import com.example.spikedash_singleplayer.Fragments.SkinsFragment;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.Managers.SoundManager;
import com.example.spikedash_singleplayer.User;
import com.example.spikedash_singleplayer.Managers.VibrationManager;
import com.google.firebase.database.FirebaseDatabase;

public class ShopActivity extends AppCompatActivity implements View.OnClickListener {
    TextView skinsTab, backgroundsTab;
    View skinsIndicator, backgroundsIndicator;
    TextView tvBalance;
    User user;
    ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        skinsTab = findViewById(R.id.skinsTab);
        backgroundsTab = findViewById(R.id.backgroundsTab);
        skinsIndicator = findViewById(R.id.skinsIndicator);
        backgroundsIndicator = findViewById(R.id.backgroundsIndicator);
        backgroundsTab.setAlpha(0.5f);
        btnBack = findViewById(R.id.btnBack);
        tvBalance = findViewById(R.id.coinBalance);
        user = getIntent().getParcelableExtra("user");
        tvBalance.setText(String.valueOf(user.getBalance()));

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
    public void refreshBalance() {
        FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid())
                .child("balance")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        long balance = snapshot.getValue(Long.class);
                        tvBalance.setText(String.valueOf(balance));
                    }
                });
    }


    @Override
    public void onClick(View v) {
        VibrationManager.vibrate(this, 25);
        SoundManager.play("click");
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
            setResult(RESULT_OK);
            finish();
        }
    }

}