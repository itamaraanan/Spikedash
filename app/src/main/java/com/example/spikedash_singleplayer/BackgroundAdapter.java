package com.example.spikedash_singleplayer;

import android.app.Dialog;
import android.content.Context;
import com.example.spikedash_singleplayer.Background;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BackgroundAdapter extends RecyclerView.Adapter<BackgroundAdapter.BackgroundViewHolder> {
    private List<Background> backgrounds;
    private Context context;
    Dialog dialog;
    private OnBackgroundRefreshRequest refreshListener;

    public BackgroundAdapter(Context context, List<Background> backgrounds) {
        this.context = context;
        this.backgrounds = backgrounds;
    }

    public void setOnBackgroundRefreshRequest(OnBackgroundRefreshRequest listener) {
        this.refreshListener = listener;
    }

    @NonNull
    @Override
    public BackgroundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_background, parent, false);
        return new BackgroundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BackgroundViewHolder holder, int position) {
        Background background = backgrounds.get(position);
        holder.name.setText(background.getName());
        holder.price.setText(background.getPrice() + " candies");

        Glide.with(context)
                .load(background.getImageUrl())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            createBuyDialog(backgrounds.get(position));
        });
    }

    public interface OnBackgroundRefreshRequest {
        void refresh();
    }

    public void createBuyDialog(Background background) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.buy_background_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

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

        btnBuy.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users").child(uid);

            userRef.get().addOnSuccessListener(snapshot -> {
                Long balance = snapshot.child("balance").getValue(Long.class);
                if (balance == null) balance = 0L;

                if (balance >= background.getPrice()) {
                    userRef.child("balance").setValue(balance - background.getPrice());
                    userRef.child("ownedBackgrounds").child(background.getBackgroundId()).setValue(true);

                    Toast.makeText(context, "Background purchased!", Toast.LENGTH_SHORT).show();
                    if (refreshListener != null) refreshListener.refresh();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Not enough candies!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show();
            });
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return backgrounds.size();
    }

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
