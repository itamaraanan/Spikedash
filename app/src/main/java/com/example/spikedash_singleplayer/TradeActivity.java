package com.example.spikedash_singleplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TradeActivity extends AppCompatActivity {

    private ImageButton btnReturn;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        // Initialize views
        btnReturn = findViewById(R.id.btnReturn);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Set up return button
        btnReturn.setOnClickListener(v -> {
            finish();
        });

        // Set up ViewPager with adapter
        setupViewPager();

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Creat Trade");
                    break;
                case 1:
                    tab.setText("Trade offers");
                    break;
            }
        }).attach();
    }

    private void setupViewPager() {
        // Create and set adapter
        TradePagerAdapter pagerAdapter = new TradePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }

    // Adapter for the ViewPager
    private static class TradePagerAdapter extends FragmentStateAdapter {

        public TradePagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new TradeFragment();
                case 1:
                    //return new FriendItemsFragment();
                default:
                    return new TradeFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2; // there are 2 tabs
        }
    }
}