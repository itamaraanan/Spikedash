package com.example.spikedash_singleplayer;

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
    private List<PlayerStats> userList = new ArrayList<>();
    private GlobalLeaderAdapter adapter;
    private RecyclerView recyclerView;
    private Dialog progressDialog;

    private void loadPlayers(){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    PlayerStats player = child.getValue(PlayerStats.class);
                    if (player != null) {
                        userList.add(player);
                    }
                }

                if (!isAdded()) return; //this prevents the app to crash if the user navigated before it loaded
                Collections.sort(userList, (a, b) -> Integer.compare(b.getHighScore(), a.getHighScore()));
                String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                adapter = new GlobalLeaderAdapter(requireContext(), userList, false, currentUid);
                recyclerView.setAdapter(adapter);

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

        loadPlayers();
        return view;
    }
}