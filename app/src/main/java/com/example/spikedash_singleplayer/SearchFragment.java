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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private String currentUid, search;
    private ImageButton btnSearch;
    private EditText etSearch;
    private List<User> userList = new ArrayList<>();

    private void loadUsers(String name) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userList.clear();

        db.get().addOnSuccessListener(snapshot -> {
            for (DataSnapshot userSnap : snapshot.getChildren()) {
                User user = userSnap.getValue(User.class);
                if (user != null &&
                        user.getUsername() != null &&
                        !user.getUid().equals(currentUid) &&
                        user.getUsername().toLowerCase().contains(name.toLowerCase())) {
                    userList.add(user);
                }
            }

            adapter = new UsersAdapter(requireContext(), userList, selectedUser -> {
                sendFriendRequest(selectedUser);
            });

            recyclerView.setAdapter(adapter);
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.rvPlayers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            if (etSearch.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                SoundManager.play("error");
                return;
            }
            search = etSearch.getText().toString();
            loadUsers(search);
        });

        return view;
    }

}