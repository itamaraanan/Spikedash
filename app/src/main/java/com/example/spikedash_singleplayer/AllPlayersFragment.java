package com.example.spikedash_singleplayer;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllPlayersFragment extends Fragment {
    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private String currentUid;
    private TextView tvPlayerCount;
    private List<User> userList = new ArrayList<>();
    private Set<String> friendUids = new HashSet<>();
    private Dialog progressDialog;

    private void loadUsers() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        userList.clear();
        friendUids = new HashSet<>();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(currentUid).child("friends").get().addOnSuccessListener(friendsSnapshot -> {
            if (friendsSnapshot.exists()) {
                for (DataSnapshot snap : friendsSnapshot.getChildren()) {
                    friendUids.add(snap.getKey());
                }
            }

            // Only load users AFTER friendUids are ready
            db.get().addOnSuccessListener(snapshot -> {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    User user = userSnap.getValue(User.class);
                    if (user != null &&
                            user.getUid() != null &&
                            !user.getUid().equals(currentUid) &&
                            !friendUids.contains(user.getUid())) {
                        userList.add(user);
                    }
                }

                adapter = new UsersAdapter(requireContext(), userList, selectedUser -> {
                    sendFriendRequest(selectedUser);
                });
                recyclerView.setAdapter(adapter);
                tvPlayerCount.setText("Total Players: " + adapter.getItemCount());
                progressDialog.dismiss();

            }).addOnFailureListener(this::errorHandler);

        }).addOnFailureListener(this::errorHandler);
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

    private void errorHandler(Exception e) {
        SoundManager.play("error");
        progressDialog.dismiss();
        Toast.makeText(getContext(), "Error loading players: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }




        @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        TextView tvMessage = progressDialog.findViewById(R.id.tvMessage);
        tvMessage.setText("Loading players...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressDialog.show();

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