package com.example.spikedash_singleplayer.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spikedash_singleplayer.Adapters.FriendAdapter;
import com.example.spikedash_singleplayer.Adapters.FriendRequestAdapter;
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

public class FriendsListFragment extends Fragment {
    RecyclerView recyclerView;
    FriendAdapter adapter;
    String currentUid;
    TextView tvFriendsCount;
    LinearLayout btnFriendRequests;
    List<User> friendsList = new ArrayList<>();
    Dialog d;
    Dialog progressDialog;

    private void loadFriends() {
        // Load friends from Firebase
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendsList.clear();

        // Get current user's friends
        usersRef.child(currentUid).child("friends").get().addOnSuccessListener(friendsSnapshot -> {
            if (friendsSnapshot.exists()) {
                Set<String> friendUids = new HashSet<>();
                for (DataSnapshot snap : friendsSnapshot.getChildren()) {
                    friendUids.add(snap.getKey());
                }
                // Now fetch user details for each friend
                usersRef.get().addOnSuccessListener(usersSnapshot -> {
                    for (DataSnapshot userSnap : usersSnapshot.getChildren()) {
                        User user = userSnap.getValue(User.class);
                        // Check if user is valid and is a friend
                        if (user != null && user.getUsername() != null && friendUids.contains(user.getUid())) {
                            friendsList.add(user);
                        }
                    }
                    if (isAdded()) { // Only if fragment is still attached to activity
                        adapter = new FriendAdapter(requireContext(), friendsList);
                        recyclerView.setAdapter(adapter);
                    }
                    tvFriendsCount.setText("Total Friends: " + friendsList.size());
                    progressDialog.dismiss();
                }).addOnFailureListener(this::errorHandler);
            }
            // If no friends found
            else {
                progressDialog.dismiss();
                tvFriendsCount.setText("Total Friends: 0");
                recyclerView.setAdapter(null);
                Toast.makeText(getContext(), "No friends found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(this::errorHandler);
    }

    private void errorHandler(Exception e) {
        SoundManager.play("error");
        progressDialog.dismiss();
        Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
    }

    private void acceptFriendRequest(String requesterUid) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        // Add the requester as a friend
        usersRef.child(currentUid).child("friends").child(requesterUid).setValue(true);
        usersRef.child(requesterUid).child("friends").child(currentUid).setValue(true);
        // Remove the friend request from both users
        usersRef.child(currentUid).child("friendRequests").child(requesterUid).removeValue();
        usersRef.child(requesterUid).child("sentFriendRequests").child(currentUid).removeValue();
    }

    private void declineFriendRequest(String requesterUid) {
        // Remove the friend request from both users
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(currentUid).child("friendRequests").child(requesterUid).removeValue();
        usersRef.child(requesterUid).child("sentFriendRequests").child(currentUid).removeValue();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //show progress dialog
        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        TextView tvMessage = progressDialog.findViewById(R.id.tvMessage);
        tvMessage.setText("Loading friends...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressDialog.show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        recyclerView = view.findViewById(R.id.rvFriends);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvFriendsCount = view.findViewById(R.id.tvFriendsCount);
        btnFriendRequests = view.findViewById(R.id.btnFriendRequests);
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadFriends();
        // Set up the friend requests button
        btnFriendRequests.setOnClickListener(v -> {
            // Open friend requests dialog
            SoundManager.play("click");
            d = new Dialog(getContext());
            d.setContentView(R.layout.requests_dialog);
            d.setCancelable(true);
            d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            d.show();
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Set up RecyclerView for friend requests
            RecyclerView rvFriendRequests = d.findViewById(R.id.rvFriendRequests);
            rvFriendRequests.setLayoutManager(new LinearLayoutManager(getContext()));
            List<User> requestList = new ArrayList<>();
            // Load friend requests from Firebase
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            DatabaseReference requestRef = usersRef.child(currentUid).child("friendRequests");
            DatabaseReference sentRef = usersRef.child(currentUid).child("sentFriendRequests");

            sentRef.get().addOnSuccessListener(sentSnapshot -> {
                // Get all sent friend request UIDs
                Set<String> sentUids = new HashSet<>();
                for (DataSnapshot snap : sentSnapshot.getChildren()) {
                    sentUids.add(snap.getKey());
                }
                requestRef.get().addOnSuccessListener(snapshot -> {
                    // Iterate through friend requests
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String requesterUid = snap.getKey();
                        if (sentUids.contains(requesterUid)) continue; // Skip if you sent it
                        // Fetch user details for the requester
                        usersRef.child(requesterUid).get().addOnSuccessListener(userSnap -> {
                            User user = userSnap.getValue(User.class);
                            if (user != null) {
                                requestList.add(user);
                                // Notify the adapter with the new data
                                rvFriendRequests.setAdapter(new FriendRequestAdapter(requestList, getContext(), new FriendRequestAdapter.OnRequestActionListener() {
                                    @Override
                                    public void onAccept(User user) {
                                        // Accept the friend request
                                        acceptFriendRequest(user.getUid());
                                        requestList.remove(user);
                                        rvFriendRequests.getAdapter().notifyDataSetChanged();
                                        Toast.makeText(getContext(), "Friend request accepted", Toast.LENGTH_SHORT).show();
                                        SoundManager.play("win");
                                        loadFriends();
                                    }

                                    @Override
                                    public void onDecline(User user) {
                                        // Decline the friend request
                                        declineFriendRequest(user.getUid());
                                        requestList.remove(user);
                                        rvFriendRequests.getAdapter().notifyDataSetChanged();
                                        Toast.makeText(getContext(), "Friend request declined", Toast.LENGTH_SHORT).show();
                                        SoundManager.play("error");
                                    }
                                }));
                            }
                        });
                    }
                });
            });

            ImageButton btnReturn = d.findViewById(R.id.btnReturn);
            btnReturn.setOnClickListener(view1 -> {
                // Close the dialog and refresh friends list
                d.dismiss();
                SoundManager.play("click");
                loadFriends();
            });
        });

        return view;
    }
}