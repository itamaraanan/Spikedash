package com.example.spikedash_singleplayer;

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
    private List<PlayerStats> friendsList = new ArrayList<>();
    private FriendsLeaderAdapter adapter;
    private RecyclerView recyclerView;
    private Dialog progressDialog;
    private String currentUid;


    private void loadFriends() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendsList.clear();

        usersRef.child(currentUid).child("friends").get().addOnSuccessListener(friendsSnapshot -> {
            if (friendsSnapshot.exists()) {
                Set<String> friendUids = new HashSet<>();
                for (DataSnapshot snap : friendsSnapshot.getChildren()) {
                    friendUids.add(snap.getKey());
                }

                usersRef.get().addOnSuccessListener(usersSnapshot -> {
                    for (DataSnapshot child : usersSnapshot.getChildren()) {
                        PlayerStats player = child.getValue(PlayerStats.class);
                        if (player != null && (friendUids.contains(player.getUid()) || player.getUid().equals(currentUid))) {
                            friendsList.add(player);
                        }
                    }

                    if (!isAdded()) return; //this prevents the app to crash if the user navigated before it loaded
                    Collections.sort(friendsList, (a, b) -> Integer.compare(b.getHighScore(), a.getHighScore()));
                    String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    adapter = new FriendsLeaderAdapter(requireContext(), friendsList, false, currentUid);
                    recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();

                }).addOnFailureListener(e -> {
                    SoundManager.play("error");
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });

            }
            //if the user dosent have any friends
        else {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "No friends found", Toast.LENGTH_SHORT).show();
                adapter = new FriendsLeaderAdapter(requireContext(), friendsList, false, currentUid);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }
        }).addOnFailureListener(e -> {
            SoundManager.play("error");
            Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
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

        loadFriends();
        return view;
    }
}