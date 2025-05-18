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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        holder.price.setText(skin.getPrice() + " candies");

        Glide.with(context)
                .load(skin.getImageUrl())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            SoundManager.play("click");
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
            SoundManager.play("click");
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users").child(uid);

            userRef.get().addOnSuccessListener(snapshot -> {
                Long balance = snapshot.child("balance").getValue(Long.class);
                if (balance == null) balance = 0L;

                if (balance >= skin.getPrice()) {
                    userRef.child("balance").setValue(balance - skin.getPrice());
                    userRef.child("ownedSkins").child(skin.getSkinId()).setValue(true);

                    Toast.makeText(context, "Skin purchased!", Toast.LENGTH_SHORT).show();
                    SoundManager.play("win");
                    if (refreshListener != null) refreshListener.refresh();
                    dialog.dismiss();
                } else {
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
