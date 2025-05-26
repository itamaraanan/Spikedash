package com.example.spikedash_singleplayer.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.SoundManager;
import com.example.spikedash_singleplayer.StorageItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class StorageSkinAdapter extends RecyclerView.Adapter<StorageSkinAdapter.ViewHolder> {
    private List<StorageItem> skinsList;
    private Context context;
    private String equippedSkinId;

    public StorageSkinAdapter(Context context, List<StorageItem> skinsList) {
        // Constructor
        this.context = context;
        this.skinsList = skinsList;
    }

    public void setEquippedSkin(String equippedSkinId) {
        this.equippedSkinId = equippedSkinId;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder for storage skins
        ImageView imageView;
        TextView nameView;
        LinearLayout contentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imSkin);
            nameView = itemView.findViewById(R.id.tvName);
            contentLayout = itemView.findViewById(R.id.cardLayout);
        }
    }

    @NonNull
    @Override
    // Create a new ViewHolder
    public StorageSkinAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.storage_skin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the view holder
        StorageItem item = skinsList.get(position);
        holder.nameView.setText(item.getName());

        if (item.getId().equals(equippedSkinId)) {
            holder.contentLayout.setBackgroundColor(Color.parseColor("#C8E6C9")); // Light green
        } else {
            holder.contentLayout.setBackgroundColor(Color.parseColor("#FFFFFF")); // White
        }

        Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.imageView);
        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String selectedSkinId = item.getId();

            // Update the equipped skin in Firebase
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("equippedSkin")
                    .setValue(selectedSkinId)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, item.getName() + " equipped!", Toast.LENGTH_SHORT).show();
                        SoundManager.play("select");
                        setEquippedSkin(selectedSkinId);
                        // Update the equipped skin ID in the adapter
                        notifyDataSetChanged();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return skinsList.size();
    }
}
