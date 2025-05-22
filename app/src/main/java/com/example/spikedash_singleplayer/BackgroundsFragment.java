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

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BackgroundsFragment extends Fragment {
    private RecyclerView recyclerView;
    private BackgroundAdapter adapter;
    private List<Background> backgroundList = new ArrayList<>();
    private Dialog progressDialog;
    private Background lastSelected;

    private void loadBackgrounds() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.child("users").child(uid).child("ownedBackgrounds").get().addOnSuccessListener(snapshot -> {
            List<String> ownedBackgroundIds = new ArrayList<>();
            for (DataSnapshot bgSnap : snapshot.getChildren()) {
                ownedBackgroundIds.add(bgSnap.getKey());
            }

            FirebaseFirestore.getInstance()
                    .collection("backgrounds")
                    .orderBy("price")
                    .get()
                    .addOnSuccessListener(querySnapshots -> {
                        backgroundList.clear();
                        for (DocumentSnapshot doc : querySnapshots) {
                            Background background = doc.toObject(Background.class);
                            if (background != null) {
                                background.setBackgroundId(doc.getId());
                                if (!ownedBackgroundIds.contains(background.getBackgroundId())) {
                                    backgroundList.add(background);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        ((ShopActicity) requireActivity()).refreshBalance();
                    }).addOnFailureListener(this::errorHandler);
        }).addOnFailureListener(this::errorHandler);
    }
    private void errorHandler(Exception e) {
        progressDialog.dismiss();
        SoundManager.play("error");
        Toast.makeText(getContext(), "Error loading backgrounds: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        tvMessage.setText("Loading store...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressDialog.show();

        View view = inflater.inflate(R.layout.fragment_backgrounds, container, false);
        recyclerView = view.findViewById(R.id.backgroundsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BackgroundAdapter(getContext(), backgroundList);
        adapter.setOnBackgroundRefreshRequest(() -> loadBackgrounds());
        recyclerView.setAdapter(adapter);
        loadBackgrounds();
        return view;
    }
}
