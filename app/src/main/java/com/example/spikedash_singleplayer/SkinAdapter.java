package com.example.spikedash_singleplayer;

import android.app.Dialog;
import android.content.Context;
import com.example.spikedash_singleplayer.Skin;

import android.content.Intent;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;

public class SkinAdapter extends RecyclerView.Adapter<SkinAdapter.SkinViewHolder> {
    private List<Skin> skins;
    private Context context;
    Dialog dialog;
    private OnSkinRefreshRequest refreshListener;
    private String loggedInUid;

    public SkinAdapter(Context context, List<Skin> skins) {
        this.context = context;
        this.skins = skins;
    }

    public void setOnSkinRefreshRequest(OnSkinRefreshRequest listener) {
        this.refreshListener = listener;
    }

    @NonNull
    @Override
    public SkinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_skin, parent, false);
        return new SkinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkinViewHolder holder, int position) {
        Skin skin = skins.get(position);
        holder.name.setText(skin.getName());
        holder.price.setText(skin.getPrice() + " coins");

        Glide.with(context)
                .load(skin.getImageUrl())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            createBuyDialog(skins.get(position));
        });

    }

    public interface OnSkinRefreshRequest {
        void refresh();
    }

    public void createBuyDialog(Skin skin) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.buy_skin_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        LinearLayout btnBuy = dialog.findViewById(R.id.btn_buy);
        LinearLayout btnCancel = dialog.findViewById(R.id.btn_cancel);
        ImageView ivSkin = dialog.findViewById(R.id.iv_skin);
        TextView tvName = dialog.findViewById(R.id.tv_skin_name);
        TextView tvPrice = dialog.findViewById(R.id.tv_skin_price);

        Glide.with(context)
                .load(skin.getImageUrl())
                .into(ivSkin);

        tvName.setText(skin.getName());
        tvPrice.setText(skin.getPrice() + " candies");

        btnBuy.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(uid).get()
                    .addOnSuccessListener(userDoc -> {
                        Long balance = userDoc.getLong("balance");
                        if (balance == null) balance = 0L;

                        if (balance != null && balance >= (long) skin.getPrice()) {
                            db.collection("users").document(uid)
                                    .update(
                                            "ownedSkins", FieldValue.arrayUnion(skin.getSkinId()),
                                            "balance", balance - skin.getPrice()
                                    )
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Skin purchased!", Toast.LENGTH_SHORT).show();
                                        if (refreshListener != null) refreshListener.refresh();
                                        dialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to update user data", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    });
                        } else {
                            Toast.makeText(context, "Not enough candies!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to check balance", Toast.LENGTH_SHORT).show();
                    });
        });



        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return skins.size();
    }

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
