package com.example.spikedash_singleplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TradeAdapter extends RecyclerView.Adapter<TradeAdapter.TradeViewHolder> {
    private List<User> users;
    private Context context;

    public TradeAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public TradeAdapter.TradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trade, parent, false);
        return new TradeAdapter.TradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TradeViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvName.setText(user.getUsername());
        uploadImage(user, holder.ivProfilePicture);

    }
    private void uploadImage(User user, ImageView imProfilePicture) {
        if (user != null && user.getBase64Image() != null && !user.getBase64Image().isEmpty()) {
            try {
                Bitmap profileBitmap = ImageUtils.decodeImage(user.getBase64Image());
                if (profileBitmap != null) {
                    imProfilePicture.setImageBitmap(profileBitmap);
                } else {
                    // Fallback to default if conversion failed
                    imProfilePicture.setImageResource(R.drawable.ic_profile);
                }
            } catch (Exception e) {
                // Handle any exceptions and use default image
                imProfilePicture.setImageResource(R.drawable.ic_profile);
                Log.e("ProfileActivity", "Error loading profile picture: " + e.getMessage());
            }
        } else {
            // Use default image if no base64 image is available
            imProfilePicture.setImageResource(R.drawable.ic_profile);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    public static class TradeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivProfilePicture;
        ImageButton btnTrade;

        public TradeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            btnTrade = itemView.findViewById(R.id.btnTrade);
            btnTrade.setOnClickListener(v -> {

            });
        }

    }

}
