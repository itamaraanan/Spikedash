package com.example.spikedash_singleplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FriendsActivity extends AppCompatActivity {

    private ImageButton btnReturn;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        btnReturn = findViewById(R.id.btnReturn);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        btnReturn.setOnClickListener(v -> {
            onBackPressed();
        });

        // Set up ViewPager with adapter
        setupViewPager();

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("All Players");
                    break;
                case 1:
                    tab.setText("Search");
                    break;
                case 2:
                    tab.setText("Friends");
                    break;
                default:
                    tab.setText("All Players");
                    break;
            }
        }).attach();

    }

    private void setupViewPager() {
        // Create and set adapter
        FriendsPagerAdapter pagerAdapter = new FriendsPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }

    // Adapter for the ViewPager
    private static class FriendsPagerAdapter extends FragmentStateAdapter {

        public FriendsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return the appropriate fragment based on position
            switch (position) {
                case 0:
                    return new AllPlayersFragment();
                case 1:
                    return new SearchFragment();
                case 2:
                    return new FriendsListFragment();
                default:
                    return new AllPlayersFragment();
            }
        }

        public int getItemCount() {
            return 3; // We now have 3 tabs
        }
    }
}