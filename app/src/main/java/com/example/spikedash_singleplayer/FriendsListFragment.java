package com.example.spikedash_singleplayer;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendsListFragment extends Fragment {
    private RecyclerView recyclerView;
    private FriendAdapter adapter;
    private String currentUid;
    private TextView tvFriendsCount;
    private LinearLayout btnFriendRequests;
    private List<User> friendsList = new ArrayList<>();
    private Dialog d;

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
                    for (DataSnapshot userSnap : usersSnapshot.getChildren()) {
                        User user = userSnap.getValue(User.class);
                        if (user != null && user.getUsername() != null && friendUids.contains(user.getUid())) {
                            friendsList.add(user);
                        }
                    }
                    if (isAdded()) { // Only if fragment is still attached to activity
                        adapter = new FriendAdapter(requireContext(), friendsList);
                        recyclerView.setAdapter(adapter);
                    }
                    tvFriendsCount.setText("Total Friends: " + friendsList.size());
                });
            }
        });
    }

    private void acceptFriendRequest(String requesterUid) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.child(currentUid).child("friends").child(requesterUid).setValue(true);
        usersRef.child(requesterUid).child("friends").child(currentUid).setValue(true);

        usersRef.child(currentUid).child("friendRequests").child(requesterUid).removeValue();
        usersRef.child(requesterUid).child("sentFriendRequests").child(currentUid).removeValue();
    }

    private void declineFriendRequest(String requesterUid) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(currentUid).child("friendRequests").child(requesterUid).removeValue();
        usersRef.child(requesterUid).child("sentFriendRequests").child(currentUid).removeValue();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

        recyclerView = view.findViewById(R.id.rvFriends);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvFriendsCount = view.findViewById(R.id.tvFriendsCount);
        btnFriendRequests = view.findViewById(R.id.btnFriendRequests);
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadFriends();

        btnFriendRequests.setOnClickListener(v -> {
            d = new Dialog(getContext());
            d.setContentView(R.layout.requests_dialog);
            d.setCancelable(true);
            d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            d.show();

            RecyclerView rvFriendRequests = d.findViewById(R.id.rvFriendRequests);
            rvFriendRequests.setLayoutManager(new LinearLayoutManager(getContext()));
            List<User> requestList = new ArrayList<>();

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            DatabaseReference requestRef = usersRef.child(currentUid).child("friendRequests");
            DatabaseReference sentRef = usersRef.child(currentUid).child("sentFriendRequests");

            sentRef.get().addOnSuccessListener(sentSnapshot -> {
                Set<String> sentUids = new HashSet<>();
                for (DataSnapshot snap : sentSnapshot.getChildren()) {
                    sentUids.add(snap.getKey());
                }

                requestRef.get().addOnSuccessListener(snapshot -> {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String requesterUid = snap.getKey();
                        if (sentUids.contains(requesterUid)) continue; // Skip if you sent it

                        usersRef.child(requesterUid).get().addOnSuccessListener(userSnap -> {
                            User user = userSnap.getValue(User.class);
                            if (user != null) {
                                requestList.add(user);
                                rvFriendRequests.setAdapter(new FriendRequestAdapter(requestList, getContext(), new FriendRequestAdapter.OnRequestActionListener() {
                                    @Override
                                    public void onAccept(User user) {
                                        acceptFriendRequest(user.getUid());
                                        requestList.remove(user);
                                        rvFriendRequests.getAdapter().notifyDataSetChanged();
                                        Toast.makeText(getContext(), "Friend request accepted", Toast.LENGTH_SHORT).show();
                                        SoundManager.play("win");
                                        loadFriends();
                                    }

                                    @Override
                                    public void onDecline(User user) {
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
                d.dismiss();
                loadFriends();
            });
        });

        return view;
    }
}