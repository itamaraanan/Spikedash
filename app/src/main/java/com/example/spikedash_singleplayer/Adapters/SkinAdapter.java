package com.example.spikedash_singleplayer.Adapters;

import android.app.Dialog;
import android.content.Context;

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
import com.example.spikedash_singleplayer.Items.StoreItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class  SkinAdapter extends RecyclerView.Adapter<SkinAdapter.SkinViewHolder> {
    private List<StoreItem> skins;
    private Context context;
    private Dialog dialog;
    private OnSkinRefreshRequest refreshListener;

    public SkinAdapter(Context context, List<StoreItem> skins) {
        //Constructor
        this.context = context;
        this.skins = skins;
    }

    public void setOnSkinRefreshRequest(OnSkinRefreshRequest listener) {
        this.refreshListener = listener;
    }

    @NonNull
    @Override
    public SkinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for skins
        View view = LayoutInflater.from(context).inflate(R.layout.item_skin, parent, false);
        return new SkinViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SkinViewHolder holder, int position) {
        // Bind data to the view holder
        StoreItem skin = skins.get(position);
        holder.name.setText(skin.getName());
        holder.price.setText(skin.getPrice() + " candies");

        Glide.with(context)
                .load(skin.getImageUrl())
                .into(holder.image);

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            SoundManager.play("click");
            createBuyDialog(skins.get(position));
        });

    }

    public interface OnSkinRefreshRequest {
        void refresh();
    }

    public void createBuyDialog(StoreItem skin) {
        // Create and show the buy dialog for skins
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.buy_skin_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Initialize dialog views
        LinearLayout btnBuy = dialog.findViewById(R.id.btn_buy);
        LinearLayout btnCancel = dialog.findViewById(R.id.btn_cancel);
        ImageView ivSkin = dialog.findViewById(R.id.iv_skin);
        TextView tvName = dialog.findViewById(R.id.tv_skin_name);
        TextView tvPrice = dialog.findViewById(R.id.tv_skin_price);

        Glide.with(context)
                .load(skin.getImageUrl())
                .into(ivSkin);
        // Initialize dialog views
        tvName.setText(skin.getName());
        tvPrice.setText(skin.getPrice() + " candies");

        btnBuy.setOnClickListener(v -> {
            // Handle buy button click
            SoundManager.play("click");
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users").child(uid);
            // Play click sound and vibrate
            userRef.get().addOnSuccessListener(snapshot -> {
                Long balance = snapshot.child("balance").getValue(Long.class);
                if (balance == null) balance = 0L;

                if (balance >= skin.getPrice()) {
                    // Deduct price from balance and add skin to ownedSkins
                    userRef.child("balance").setValue(balance - skin.getPrice());
                    userRef.child("ownedSkins").child(skin.getId()).setValue(true);

                    Toast.makeText(context, "Skin purchased!", Toast.LENGTH_SHORT).show();
                    SoundManager.play("win");
                    if (refreshListener != null) refreshListener.refresh();
                    dialog.dismiss();
                } else {
                    // Not enough balance
                    SoundManager.play("error");
                    Toast.makeText(context, "Not enough candies!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                SoundManager.play("error");
                Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show();
            });
        });



        btnCancel.setOnClickListener(v -> {
            SoundManager.play("click");
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return skins.size();
    }
    // ViewHolder class for the skin items
    static class SkinViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image;

        public SkinViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            price = itemView.findViewById(R.id.tvPrice);
            image = itemView.findViewById(R.id.imSkin);
        }
    }
}
