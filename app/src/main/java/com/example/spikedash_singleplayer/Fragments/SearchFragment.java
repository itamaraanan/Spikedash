package com.example.spikedash_singleplayer.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.spikedash_singleplayer.Adapters.UsersAdapter;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.SoundManager;
import com.example.spikedash_singleplayer.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFragment extends Fragment {
    RecyclerView recyclerView;
    UsersAdapter adapter;
    String currentUid, search;
    ImageButton btnSearch;
    EditText etSearch;
    List<User> userList = new ArrayList<>();
    Set<String> friendUids;

    private void loadUsers(String name) {
        // Load users from Firebase based on the search term
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userList.clear();
        friendUids = new HashSet<>();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        // First, load the list of friend UIDs
        usersRef.child(currentUid).child("friends").get().addOnSuccessListener(friendsSnapshot -> {
            if (friendsSnapshot.exists()) {
                for (DataSnapshot snap : friendsSnapshot.getChildren()) {
                    friendUids.add(snap.getKey());
                }
            }

            // Now load all users AFTER friends are known
            userList.clear();
            usersRef.get().addOnSuccessListener(snapshot -> {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    User user = userSnap.getValue(User.class);
                    // Check if user is valid and not the current user or a friend
                    if (user != null &&
                            user.getUsername() != null &&
                            !user.getUid().equals(currentUid) &&
                            !friendUids.contains(user.getUid()) &&
                            // Check if username contains the search term
                            user.getUsername().toLowerCase().contains(name.toLowerCase())) {
                        userList.add(user);
                    }
                }
                // Notify the adapter that data has changed
                adapter.notifyDataSetChanged();

            }).addOnFailureListener(e -> {
                SoundManager.play("error");
                Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
            });

        }).addOnFailureListener(e -> {
            SoundManager.play("error");
            Toast.makeText(getContext(), "Failed to load friend list", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendFriendRequest(User targetUser) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Add current user to target user's 'friendRequests'
        usersRef.child(targetUser.getUid())
                .child("friendRequests")
                .child(currentUid)
                .setValue(true);

        // Add target user to current user's 'sentFriendRequests'
        usersRef.child(currentUid)
                .child("sentFriendRequests")
                .child(targetUser.getUid())
                .setValue(true)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Friend request sent!", Toast.LENGTH_SHORT).show();
                    SoundManager.play("win");
                })
                .addOnFailureListener(e -> {
                    SoundManager.play("error");
                    Toast.makeText(getContext(), "Failed to send request", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        // Initialize RecyclerView and other UI elements
        recyclerView = view.findViewById(R.id.rvPlayers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        // Set up the search button click listener
        btnSearch.setOnClickListener(v -> {
            if (etSearch.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                SoundManager.play("error");
                return;
            }
            search = etSearch.getText().toString();
            loadUsers(search);
        });
        // Initialize the adapter with an empty user list
        adapter = new UsersAdapter(requireContext(), userList, selectedUser -> sendFriendRequest(selectedUser));
        recyclerView.setAdapter(adapter);

        return view;
    }

}