package com.example.spikedash_singleplayer.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spikedash_singleplayer.ImageUtils;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.User;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private List<User> users;
    private Context context;

    public FriendAdapter(Context context, List<User> users) {
        // Constructor
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public FriendAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new ViewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new FriendAdapter.FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        // Bind data to the view holder
        User user = users.get(position);
        holder.tvName.setText(user.getUsername());
        uploadImage(user, holder.ivProfilePicture);

    }
    private void uploadImage(User user, ImageView imProfilePicture) {
        // Check if the user has a valid base64 image
        if (user != null && user.getBase64Image() != null && !user.getBase64Image().isEmpty()) {
            try {// Decode the base64 image and set it to the ImageView
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
    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder for friends
        TextView tvName;
        ImageView ivProfilePicture;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
        }

    }

}
