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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SkinsFragment extends Fragment {
    private RecyclerView recyclerView;
    private SkinAdapter adapter;
    private List<Skin> skinList = new ArrayList<>();
    private Skin lastSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skins, container, false);
        recyclerView = view.findViewById(R.id.skinsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SkinAdapter(getContext(), skinList);
        recyclerView.setAdapter(adapter);
        FirebaseFirestore.getInstance()
                .collection("skins")
                .orderBy("price")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    skinList.clear();
                    for (DocumentSnapshot doc : querySnapshots) {
                        Skin skin = doc.toObject(Skin.class);
                        if (skin != null) {
                            Log.d("SkinsFragment", "Loaded skin: " + skin.getName());
                            skinList.add(skin);
                        } else {
                            Log.w("SkinsFragment", "Failed to load skin from: " + doc.getId());
                        }
                    }
                    adapter.notifyDataSetChanged();
                });


        return view;
    }

}
