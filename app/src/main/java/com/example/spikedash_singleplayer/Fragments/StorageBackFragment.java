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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spikedash_singleplayer.Adapters.StorageBackAdapter;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.Managers.SoundManager;
import com.example.spikedash_singleplayer.Items.StorageItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
public class StorageBackFragment extends Fragment {
    RecyclerView recyclerView;
    Dialog progressDialog;
    StorageBackAdapter adapter;
    List<StorageItem> backgroundList = new ArrayList<>();

    private void loadOwnedBackgrounds() {
        // Load owned backgrounds from Firebase
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        // Get owned backgrounds from Realtime Database
        userRef.child("ownedBackgrounds").get().addOnSuccessListener(snapshot -> {
            List<String> ownedIds = new ArrayList<>();
            for (DataSnapshot bg : snapshot.getChildren()) {
                ownedIds.add(bg.getKey()); // backgroundId
            }
            // Get equipped background from Realtime Database
            userRef.child("equippedBackground").get().addOnSuccessListener(equippedSnap -> {
                String equippedId = equippedSnap.getValue(String.class); // this is now the ID

                // Now load background definitions from Firestore
                FirebaseFirestore.getInstance()
                        .collection("backgrounds")
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            backgroundList.clear();
                            // Iterate through Firestore documents
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                String id = doc.getId(); // backgroundId
                                String name = doc.getString("name");
                                String imageUrl = doc.getString("imageUrl");

                                if (ownedIds.contains(id)) {
                                    // Only add if the user owns this background
                                    backgroundList.add(new StorageItem(id, name, imageUrl));
                                }
                            }
                            // Set up the adapter with the loaded backgrounds
                            adapter.setEquippedBackground(equippedId);
                            adapter.notifyDataSetChanged();
                            progressDialog.dismiss();


                        }).addOnFailureListener(this::errorHandler);
            }).addOnFailureListener(this::errorHandler);
        }).addOnFailureListener(this::errorHandler);
    }

    private void errorHandler(Exception e) {
        SoundManager.play("error");
        progressDialog.dismiss();
        Toast.makeText(getContext(), "Error loading backgrounds: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //call the progress dialog
        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        TextView tvMessage = progressDialog.findViewById(R.id.tvMessage);
        tvMessage.setText("Loading storage...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressDialog.show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_storage_back, container, false);
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.backgroundsRecyclerView);
        // Set up RecyclerView with GridLayoutManager
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        // Initialize the adapter
        adapter = new StorageBackAdapter(getContext(), backgroundList);
        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);
        // Load owned backgrounds from Firebase
        loadOwnedBackgrounds();

        return view;
    }
}
