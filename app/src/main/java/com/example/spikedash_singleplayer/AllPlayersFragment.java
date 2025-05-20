package com.example.spikedash_singleplayer;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import java.util.List;

public class AllPlayersFragment extends Fragment {
    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private String currentUid;
    private TextView tvPlayerCount;
    private List<User> userList = new ArrayList<>();

    private void loadUsers() {
        // Load users from Firebase to the fragment
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.get().addOnSuccessListener(snapshot -> {
            for (DataSnapshot userSnap : snapshot.getChildren()) {
                User user = userSnap.getValue(User.class);
                if (user != null && !user.getUid().equals(currentUid)) {
                    userList.add(user);
                }
            }
            adapter = new UsersAdapter(requireContext(), userList, selectedUser -> {
                sendFriendRequest(selectedUser);
            });

            recyclerView.setAdapter(adapter);
            tvPlayerCount.setText("Total Players: " + adapter.getItemCount());
        });
    }
    private void sendFriendRequest(User targetUser) {
        //sending friend request to another user
        //adding the current user to the target user's friend request list
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(targetUser.getUid())
                .child("friendRequests")
                .child(currentUid);
        //adding onSuccessListener and onFailureListener
        ref.setValue(true).addOnSuccessListener(unused -> {
            Toast.makeText(getContext(), "Friend request sent!", Toast.LENGTH_SHORT).show();
            SoundManager.play("win");


        }).addOnFailureListener(e -> {
            SoundManager.play("error");
            Toast.makeText(getContext(), "Failed to send request", Toast.LENGTH_SHORT).show();
        });

    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_players, container, false);
        recyclerView = view.findViewById(R.id.rvAllPlayers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvPlayerCount = view.findViewById(R.id.tvPlayerCount);
        userList = new ArrayList<>();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadUsers();
        return view;
    }

}