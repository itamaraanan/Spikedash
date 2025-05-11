package com.example.spikedash_singleplayer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;


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
    private List<Skin> skinList = new ArrayList<>();
    private Skin lastSelected;
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
                    });
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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
