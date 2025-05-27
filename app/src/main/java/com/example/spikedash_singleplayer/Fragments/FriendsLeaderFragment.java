package com.example.spikedash_singleplayer.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spikedash_singleplayer.Adapters.FriendsLeaderAdapter;
import com.example.spikedash_singleplayer.Items.PlayerStats;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.Managers.SoundManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendsLeaderFragment extends Fragment {
    List<PlayerStats> friendsList = new ArrayList<>();
    FriendsLeaderAdapter adapter;
    RecyclerView recyclerView;
    Dialog progressDialog;
    String currentUid;


    private void loadFriends() {
        // Fetching the current user's friends from Firebase Realtime Database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendsList.clear();

        // Getting the friends of the current user
        usersRef.child(currentUid).child("friends").get().addOnSuccessListener(friendsSnapshot -> {
            if (friendsSnapshot.exists()) {
                Set<String> friendUids = new HashSet<>();
                // Looping through the friends' UIDs and adding them to a set
                for (DataSnapshot snap : friendsSnapshot.getChildren()) {
                    friendUids.add(snap.getKey());
                }
                // If the user has friends, fetch their data
                usersRef.get().addOnSuccessListener(usersSnapshot -> {
                    for (DataSnapshot child : usersSnapshot.getChildren()) {
                        PlayerStats player = child.getValue(PlayerStats.class);
                        if (player != null && (friendUids.contains(player.getUid()) || player.getUid().equals(currentUid))) {
                            // Adding the player to the friends list if they are a friend or the current user
                            friendsList.add(player);
                        }
                    }
                    // Sorting the friends list based on high score in descending order
                    Collections.sort(friendsList, (a, b) -> Integer.compare(b.getHighScore(), a.getHighScore()));
                    // Creating the adapter with the friends list and current user ID
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();

                }).addOnFailureListener(this::errorHandler);
            }
        else {
            // If the user has no friends, show a message and set an empty adapter
                if (!isAdded()) return;
                Toast.makeText(getContext(), "No friends found", Toast.LENGTH_SHORT).show();
                adapter = new FriendsLeaderAdapter(requireContext(), friendsList,  currentUid);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }
        }).addOnFailureListener(this::errorHandler);
    }
    private void errorHandler(Exception e) {
        // Handle the error
        SoundManager.play("error");
        Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
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
        View view = inflater.inflate(R.layout.fragment_friends_leader, container, false);
        recyclerView = view.findViewById(R.id.rvFriendsLeader);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //setting separation line between the items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                LinearLayoutManager.VERTICAL
        );
        recyclerView.addItemDecoration(dividerItemDecoration);
        friendsList = new ArrayList<>();
        // Getting the current user's UID
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Creating the adapter with the friends list and current user ID
        adapter = new FriendsLeaderAdapter(requireContext(), friendsList, currentUid);
        // Setting the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        loadFriends();
        return view;
    }
}