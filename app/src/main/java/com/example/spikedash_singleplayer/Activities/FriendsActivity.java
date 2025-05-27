package com.example.spikedash_singleplayer.Activities;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spikedash_singleplayer.Fragments.AllPlayersFragment;
import com.example.spikedash_singleplayer.Fragments.FriendsListFragment;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.Fragments.SearchFragment;
import com.example.spikedash_singleplayer.Managers.SoundManager;
import com.example.spikedash_singleplayer.Managers.VibrationManager;
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
            SoundManager.play("click");
            VibrationManager.vibrate(this, 25);
            setResult(RESULT_OK);
            finish();
        });
        //adapter that return the fragments for each tab
        viewPager.setAdapter(new FriendsPagerAdapter(this));

        //set up the tab layout with the view pager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("All Players");
            } else if (position == 1) {
                tab.setText("Search");
            } else if (position == 2) {
                tab.setText("Friends");
            } else {
                tab.setText("All Players");
            }
        }).attach();

    }
    // This adapter manages the fragments for the ViewPager2 in FriendsActivity
    private static class FriendsPagerAdapter extends FragmentStateAdapter {

        // Constructor that takes a FragmentActivity to manage the fragments
        public FriendsPagerAdapter(@NonNull FragmentActivity activity) {
            super(activity);
        }

        // This method creates the fragment for each position in the ViewPager2
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Depending on the position, return the appropriate fragment
            if (position == 0) {
                return new AllPlayersFragment();
            } else if (position == 1) {
                return new SearchFragment();
            } else if (position == 2) {
                return new FriendsListFragment();
            } else {
                return new AllPlayersFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
