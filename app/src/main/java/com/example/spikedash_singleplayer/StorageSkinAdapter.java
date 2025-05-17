package com.example.spikedash_singleplayer;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class StorageSkinAdapter extends RecyclerView.Adapter<StorageSkinAdapter.ViewHolder> {
    private List<StorageItem> skinsList;
    private Context context;
    private String equippedSkinId;

    public StorageSkinAdapter(Context context, List<StorageItem> skinsList) {
        this.context = context;
        this.skinsList = skinsList;
    }

    public void setEquippedSkin(String equippedSkinId) {
        this.equippedSkinId = equippedSkinId;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
    public StorageSkinAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.storage_skin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

        holder.itemView.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String selectedSkinId = item.getId();

            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("equippedSkin")
                    .setValue(selectedSkinId)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, item.getName() + " equipped!", Toast.LENGTH_SHORT).show();
                        SoundManager.play("select");
                        setEquippedSkin(selectedSkinId);
                        notifyDataSetChanged();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return skinsList.size();
    }
}
