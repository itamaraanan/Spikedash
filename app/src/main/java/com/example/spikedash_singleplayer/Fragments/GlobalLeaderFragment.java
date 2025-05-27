package com.example.spikedash_singleplayer.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spikedash_singleplayer.Adapters.GlobalLeaderAdapter;
import com.example.spikedash_singleplayer.Items.PlayerStats;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.Managers.SoundManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlobalLeaderFragment extends Fragment {
    List<PlayerStats> userList = new ArrayList<>();
    GlobalLeaderAdapter adapter;
    RecyclerView recyclerView;
    Dialog progressDialog;
    String currentUid;

    private void loadPlayers(){
        // Fetching data from Firebase Realtime Database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
        // Adding a listener to fetch data once
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    // Each child represents a user
                    PlayerStats player = child.getValue(PlayerStats.class);
                    if (player != null) {
                        userList.add(player);
                    }
                }
                // Sorting the list based on high score in descending order
                Collections.sort(userList, (a, b) -> Integer.compare(b.getHighScore(), a.getHighScore()));
                // Notifying the adapter that the data has changed
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SoundManager.play("error");
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //creating loading screen because it takes time to load all users
        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        TextView tvMessage = progressDialog.findViewById(R.id.tvMessage);
        tvMessage.setText("Loading leaderboard...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        progressDialog.show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_global_leader, container, false);
        recyclerView = view.findViewById(R.id.rvGlobalLeader);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //setting separation line between the items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                LinearLayoutManager.VERTICAL
        );
        recyclerView.addItemDecoration(dividerItemDecoration);
        userList = new ArrayList<>();

        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Creating the adapter with the user list and current user ID
        adapter = new GlobalLeaderAdapter(requireContext(), userList, currentUid);
        recyclerView.setAdapter(adapter);


        loadPlayers();
        return view;
    }
}