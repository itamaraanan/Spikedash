package com.example.spikedash_singleplayer.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spikedash_singleplayer.Adapters.BackgroundAdapter;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.Activities.ShopActivity;
import com.example.spikedash_singleplayer.Managers.SoundManager;
import com.example.spikedash_singleplayer.Items.StoreItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BackgroundsFragment extends Fragment {
    RecyclerView recyclerView;
    BackgroundAdapter adapter;
    List<StoreItem> backgroundList = new ArrayList<>();
    Dialog progressDialog;

    private void loadBackgrounds() {
        // Load backgrounds from Firebase
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        // Get owned backgrounds from Realtime Database
        db.child("users").child(uid).child("ownedBackgrounds").get().addOnSuccessListener(snapshot -> {
            List<String> ownedBackgroundIds = new ArrayList<>();
            // Collect owned background IDs
            for (DataSnapshot bgSnap : snapshot.getChildren()) {
                ownedBackgroundIds.add(bgSnap.getKey());
            }
            // Query Firestore for available backgrounds
            FirebaseFirestore.getInstance()
                    .collection("backgrounds")
                    .orderBy("price")
                    .get()
                    .addOnSuccessListener(querySnapshots -> {
                        backgroundList.clear();
                        // Iterate through Firestore documents
                        for (DocumentSnapshot doc : querySnapshots) {
                            // Convert document to StoreItem
                            StoreItem background = doc.toObject(StoreItem.class);
                            if (background != null) {
                                background.setId(doc.getId());
                                // Add background to list if not owned
                                if (!ownedBackgroundIds.contains(background.getId())) {
                                    backgroundList.add(background);
                                }
                            }
                        }
                        // Notify adapter of data change
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        // Refresh balance in the activity
                        ((ShopActivity) requireActivity()).refreshBalance();
                    }).addOnFailureListener(this::errorHandler);
        }).addOnFailureListener(this::errorHandler);
    }
    private void errorHandler(Exception e) {
        // Handle errors during loading
        progressDialog.dismiss();
        SoundManager.play("error");
        Toast.makeText(getContext(), "Error loading backgrounds: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set up RecyclerView
        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        TextView tvMessage = progressDialog.findViewById(R.id.tvMessage);
        tvMessage.setText("Loading store...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressDialog.show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_backgrounds, container, false);

        recyclerView = view.findViewById(R.id.backgroundsRecyclerView);
        // Set up RecyclerView with LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Initialize the adapter
        adapter = new BackgroundAdapter(getContext(), backgroundList);
        //set onBackgroundRefreshRequest to refresh when needed
        adapter.setOnBackgroundRefreshRequest(() -> loadBackgrounds());
        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);
        loadBackgrounds();
        return view;
    }
}
