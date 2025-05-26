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

import com.example.spikedash_singleplayer.ImageUtils;
import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.User;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.RequestViewHolder> {
    public interface OnRequestActionListener {
        void onAccept(User user);
        void onDecline(User user);
    }

    private List<User> requestList;
    private Context context;
    private OnRequestActionListener listener;

    public FriendRequestAdapter(List<User> requestList, Context context, OnRequestActionListener listener) {
        //
        this.requestList = requestList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_item, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        // Bind data to the view holder
        User user = requestList.get(position);
        holder.tvName.setText(user.getUsername());
        uploadImage(user, holder.ivProfilePicture);

        holder.btnAccept.setOnClickListener(v -> listener.onAccept(user));
        holder.btnDecline.setOnClickListener(v -> listener.onDecline(user));
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
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageButton btnAccept, btnDecline;
        ImageView ivProfilePicture;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
        }
    }
}
