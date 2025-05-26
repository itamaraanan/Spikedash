package com.example.spikedash_singleplayer.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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

import com.example.spikedash_singleplayer.Adapters.SkinAdapter;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.Activities.ShopActivity;
import com.example.spikedash_singleplayer.StoreItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SkinsFragment extends Fragment {
    RecyclerView recyclerView;
    SkinAdapter adapter;
    Dialog progressDialog;
    List<StoreItem> skinList = new ArrayList<>();

    private void loadSkins() {
        // Load skins from Firebase
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        // Get owned skins from Realtime Database
        db.child("users").child(uid).child("ownedSkins").get().addOnSuccessListener(snapshot -> {
            List<String> ownedSkinIds = new ArrayList<>();
            // Collect owned skin IDs
            for (DataSnapshot skinSnap : snapshot.getChildren()) {
                ownedSkinIds.add(skinSnap.getKey());
            }
            // Query Firestore for available skins
            FirebaseFirestore.getInstance()
                    .collection("skins")
                    .orderBy("price")
                    .get()
                    .addOnSuccessListener(querySnapshots -> {
                        skinList.clear();
                        // Iterate through Firestore documents
                        for (DocumentSnapshot doc : querySnapshots) {
                            // Convert document to StoreItem
                            StoreItem skin = doc.toObject(StoreItem.class);
                            if (skin != null) {
                                skin.setId(doc.getId());
                                // Add skin to list if not owned
                                if (!ownedSkinIds.contains(skin.getId())) {
                                    skinList.add(skin);
                                }
                            }
                        }
                        // Notify adapter of data change
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        // Refresh balance in ShopActivity if available
                        if (isAdded() && getActivity() instanceof ShopActivity) {
                            ((ShopActivity) getActivity()).refreshBalance();
                        }
                    }).addOnFailureListener(this::errorHandler);
        }).addOnFailureListener(this::errorHandler);
    }

    private void errorHandler(Exception e) {
        // Handle errors during loading
        Log.e("FirebaseError", "Error: " + e.getMessage());
        Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Initialize the progress dialog
        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        TextView tvMessage = progressDialog.findViewById(R.id.tvMessage);
        tvMessage.setText("Loading store...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressDialog.show();

        // Inflate the layout for this fragment and set up RecyclerView
        View view = inflater.inflate(R.layout.fragment_skins, container, false);
        recyclerView = view.findViewById(R.id.skinsRecyclerView);
        // Set up RecyclerView with LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Initialize the adapter
        adapter = new SkinAdapter(getContext(), skinList);
        // Set onSkinRefreshRequest to refresh when needed
        adapter.setOnSkinRefreshRequest(() -> loadSkins());
        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);
        // Load skins from Firebase
        loadSkins();
        return view;
    }




}
