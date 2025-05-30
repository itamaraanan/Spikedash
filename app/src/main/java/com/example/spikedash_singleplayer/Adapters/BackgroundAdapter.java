package com.example.spikedash_singleplayer.Adapters;

import android.app.Dialog;
import android.content.Context;

import com.example.spikedash_singleplayer.R;
import com.example.spikedash_singleplayer.Managers.SoundManager;
import com.example.spikedash_singleplayer.Items.StoreItem;

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
import com.example.spikedash_singleplayer.Managers.VibrationManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BackgroundAdapter extends RecyclerView.Adapter<BackgroundAdapter.BackgroundViewHolder> {
    private List<StoreItem> backgrounds;
    private Context context;
    private Dialog dialog;
    private OnBackgroundRefreshRequest refreshListener;

    public BackgroundAdapter(Context context, List<StoreItem> backgrounds) {
        // Constructor
        this.context = context;
        this.backgrounds = backgrounds;
    }

    public void setOnBackgroundRefreshRequest(OnBackgroundRefreshRequest listener) {
        this.refreshListener = listener;
    }

    @NonNull
    @Override
    public BackgroundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for backgrounds
        View view = LayoutInflater.from(context).inflate(R.layout.item_background, parent, false);
        return new BackgroundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BackgroundViewHolder holder, int position) {
        // Bind data to the view holder
        StoreItem background = backgrounds.get(position);
        holder.name.setText(background.getName());
        holder.price.setText(background.getPrice() + " candies");

        Glide.with(context)
                .load(background.getImageUrl())
                .into(holder.image);
        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            SoundManager.play("click");
            createBuyDialog(backgrounds.get(position));
        });
    }

    public interface OnBackgroundRefreshRequest {
        void refresh();
    }

    public void createBuyDialog(StoreItem background) {
        // Create and show the buy dialog for backgrounds
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.buy_background_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Initialize dialog views
        LinearLayout btnBuy = dialog.findViewById(R.id.btn_buy);
        LinearLayout btnCancel = dialog.findViewById(R.id.btn_cancel);
        ImageView ivBackground = dialog.findViewById(R.id.iv_background);
        TextView tvName = dialog.findViewById(R.id.tv_background_name);
        TextView tvPrice = dialog.findViewById(R.id.tv_background_price);

        Glide.with(context)
                .load(background.getImageUrl())
                .into(ivBackground);

        tvName.setText(background.getName());
        tvPrice.setText(background.getPrice() + " candies");

        // Set click listeners for buy and cancel buttons
        btnBuy.setOnClickListener(v -> {
            // Play click sound and vibrate
            VibrationManager.vibrate(context, 25);
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users").child(uid);

            // Check user's balance
            userRef.get().addOnSuccessListener(snapshot -> {
                Long balance = snapshot.child("balance").getValue(Long.class);
                if (balance == null) balance = 0L;

                // If balance is sufficient, deduct the price and mark background as owned
                if (balance >= background.getPrice()) {
                    userRef.child("balance").setValue(balance - background.getPrice());
                    userRef.child("ownedBackgrounds").child(background.getId()).setValue(true);

                    Toast.makeText(context, "Background purchased!", Toast.LENGTH_SHORT).show();
                    SoundManager.play("win");
                    if (refreshListener != null) refreshListener.refresh();
                    dialog.dismiss();
                } else {
                    // If not enough balance, show error message
                    SoundManager.play("error");
                    Toast.makeText(context, "Not enough candies!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                SoundManager.play("error");
                Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show();
            });
        });

        btnCancel.setOnClickListener(v -> {
            VibrationManager.vibrate(context, 25);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return backgrounds.size();
    }

    // ViewHolder class for backgrounds
    static class BackgroundViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image;

        public BackgroundViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            price = itemView.findViewById(R.id.tvPrice);
            image = itemView.findViewById(R.id.imBackground);
        }
    }
}
