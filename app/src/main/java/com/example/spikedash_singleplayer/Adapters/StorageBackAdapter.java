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
import com.example.spikedash_singleplayer.Managers.SoundManager;
import com.example.spikedash_singleplayer.Items.StorageItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class StorageBackAdapter extends RecyclerView.Adapter<StorageBackAdapter.ViewHolder> {
    private List<StorageItem> backgroundsList;
    private Context context;
    private String equippedBackgroundId;

    public StorageBackAdapter(Context context, List<StorageItem> backgroundsList) {
        // Constructor
        this.context = context;
        this.backgroundsList = backgroundsList;
    }

    public void setEquippedBackground(String equippedBackgroundId) {
        this.equippedBackgroundId = equippedBackgroundId;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder for storage backgrounds
        ImageView imageView;
        TextView nameView;
        LinearLayout contentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imBackground);
            nameView = itemView.findViewById(R.id.tvName);
            contentLayout = itemView.findViewById(R.id.cardLayout);
        }
    }

    @NonNull
    @Override
    // Create a new ViewHolder
    public StorageBackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.storage_background, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the view holder
        StorageItem item = backgroundsList.get(position);
        holder.nameView.setText(item.getName());

        // Highlight the currently equipped background
        if (item.getId().equals(equippedBackgroundId)) {
            holder.contentLayout.setBackgroundColor(Color.parseColor("#C8E6C9")); // Light green
        } else {
            holder.contentLayout.setBackgroundColor(Color.parseColor("#FFFFFF")); // White
        }

        Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.imageView);
        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            SoundManager.play("select");
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String selectedBackgroundId = item.getId(); // use ID for storage

            // Update the equipped background in Firebase
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("equippedBackground")
                    .setValue(selectedBackgroundId)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, item.getName() + " equipped!", Toast.LENGTH_SHORT).show();
                        setEquippedBackground(selectedBackgroundId);
                        // Update the equipped background ID in the adapter
                        notifyDataSetChanged();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return backgroundsList.size();
    }
}
