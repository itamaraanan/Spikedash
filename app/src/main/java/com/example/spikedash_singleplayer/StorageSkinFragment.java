package com.example.spikedash_singleplayer;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class StorageSkinFragment extends Fragment {

    private RecyclerView recyclerView;
    private StorageSkinAdapter adapter;
    private Dialog progressDialog;
    private List<StorageItem> skinList = new ArrayList<>();

    private void loadOwnedSkins() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.child("ownedSkins").get().addOnSuccessListener(snapshot -> {
            List<String> ownedIds = new ArrayList<>();
            for (DataSnapshot skin : snapshot.getChildren()) {
                ownedIds.add(skin.getKey()); // skinId
            }

            userRef.child("equippedSkin").get().addOnSuccessListener(equippedSnap -> {
                String equippedId = equippedSnap.getValue(String.class); // skinId

                FirebaseFirestore.getInstance()
                        .collection("skins")
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            skinList.clear();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                String id = doc.getId(); // skinId
                                String name = doc.getString("name");
                                String imageUrl = doc.getString("imageUrl");

                                if (ownedIds.contains(id)) {
                                    skinList.add(new StorageItem(id, name, imageUrl));
                                }
                            }

                            adapter.setEquippedSkin(equippedId);
                            adapter.notifyDataSetChanged();
                        }).addOnFailureListener(this::errorHandler);
            }).addOnFailureListener(this::errorHandler);
        }).addOnFailureListener(this::errorHandler);
    }
    private void errorHandler(Exception e) {
        SoundManager.play("error");
        progressDialog.dismiss();
        Toast.makeText(getContext(), "Error loading skins: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        TextView tvMessage = progressDialog.findViewById(R.id.tvMessage);
        tvMessage.setText("Loading storage...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressDialog.show();

        View view = inflater.inflate(R.layout.fragment_storage_skin, container, false);

        recyclerView = view.findViewById(R.id.skinsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapter = new StorageSkinAdapter(getContext(), skinList);
        recyclerView.setAdapter(adapter);

        loadOwnedSkins();

        return view;
    }
}
