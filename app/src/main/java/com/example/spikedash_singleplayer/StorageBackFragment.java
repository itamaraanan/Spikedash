package com.example.spikedash_singleplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
public class StorageBackFragment extends Fragment {

    private RecyclerView recyclerView;
    private StorageBackAdapter adapter;
    private List<StorageItem> backgroundList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storage_back, container, false);

        recyclerView = view.findViewById(R.id.backgroundsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapter = new StorageBackAdapter(getContext(), backgroundList);
        recyclerView.setAdapter(adapter);

        loadOwnedBackgrounds();

        return view;
    }

    private void loadOwnedBackgrounds() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.child("ownedBackgrounds").get().addOnSuccessListener(snapshot -> {
            List<String> ownedIds = new ArrayList<>();
            for (DataSnapshot bg : snapshot.getChildren()) {
                ownedIds.add(bg.getKey()); // backgroundId
            }

            userRef.child("equippedBackground").get().addOnSuccessListener(equippedSnap -> {
                String equippedId = equippedSnap.getValue(String.class); // this is now the ID

                // Now load background definitions from Firestore
                FirebaseFirestore.getInstance()
                        .collection("backgrounds")
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            backgroundList.clear();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                String id = doc.getId(); // backgroundId
                                String name = doc.getString("name");
                                String imageUrl = doc.getString("imageUrl");

                                if (ownedIds.contains(id)) {
                                    backgroundList.add(new StorageItem(id, name, imageUrl));
                                }
                            }

                            adapter.setEquippedBackground(equippedId);
                            adapter.notifyDataSetChanged();
                        });
            });
        });
    }
}
