package com.example.spikedash_singleplayer.Adapters;

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

import com.example.spikedash_singleplayer.Managers.ImageUtils;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private List<User> users;
    private Context context;
    private OnSendRequestListener requestListener;

    public interface OnSendRequestListener {
        void onSendRequest(User user);
    }
    public UsersAdapter(Context context, List<User> users, OnSendRequestListener listener) {
        this.context = context;
        this.users = users;
        this.requestListener = listener;
    }
    @NonNull
    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player, parent, false);
        return new UsersAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Bind data to the view holder
        User user = users.get(position);
        holder.tvName.setText(user.getUsername());
        uploadImage(user, holder.ivProfilePicture);

        holder.btnAddFriend.setOnClickListener(v -> {
            if (requestListener != null) {
                requestListener.onSendRequest(user);
            }
        });
    }
    private void uploadImage(User user, ImageView imProfilePicture) {
        // Check if the user has a valid base64 image
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
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder for users
        TextView tvName;
        ImageView ivProfilePicture;
        ImageButton btnAddFriend;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
        }

    }

}
