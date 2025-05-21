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

public class TradeFragment extends Fragment {
    private RecyclerView recyclerView;
    private TradeAdapter adapter;
    private String currentUid;
    private List<User> friendsList = new ArrayList<>();

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
                        adapter = new TradeAdapter(requireContext(), friendsList);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trade, container, false);

        recyclerView = view.findViewById(R.id.rvFriends);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadFriends();

        return view;
    }
}