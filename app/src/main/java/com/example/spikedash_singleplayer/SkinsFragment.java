package com.example.spikedash_singleplayer;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class SkinsFragment extends Fragment {
    private RecyclerView recyclerView;
    private SkinAdapter adapter;
    private Dialog progressDialog;
    private List<Skin> skinList = new ArrayList<>();

    private void loadSkins() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.child("users").child(uid).child("ownedSkins").get().addOnSuccessListener(snapshot -> {
            List<String> ownedSkinIds = new ArrayList<>();
            for (DataSnapshot skinSnap : snapshot.getChildren()) {
                ownedSkinIds.add(skinSnap.getKey());
            }

            FirebaseFirestore.getInstance()
                    .collection("skins")
                    .orderBy("price")
                    .get()
                    .addOnSuccessListener(querySnapshots -> {
                        skinList.clear();
                        for (DocumentSnapshot doc : querySnapshots) {
                            Skin skin = doc.toObject(Skin.class);
                            if (skin != null) {
                                skin.setSkinId(doc.getId());
                                if (!ownedSkinIds.contains(skin.getSkinId())) {
                                    skinList.add(skin);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        if (isAdded() && getActivity() instanceof ShopActicity) {
                            ((ShopActicity) getActivity()).refreshBalance();
                        }
                    }).addOnFailureListener(this::errorHandler);
        }).addOnFailureListener(this::errorHandler);
    }

    private void errorHandler(Exception e) {
        Log.e("FirebaseError", "Error: " + e.getMessage());
        Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
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

        View view = inflater.inflate(R.layout.fragment_skins, container, false);
        recyclerView = view.findViewById(R.id.skinsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SkinAdapter(getContext(), skinList);
        adapter.setOnSkinRefreshRequest(() -> loadSkins());
        recyclerView.setAdapter(adapter);
        loadSkins();
        return view;
    }




}
