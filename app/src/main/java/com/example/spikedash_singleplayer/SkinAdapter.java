package com.example.spikedash_singleplayer;

import android.content.Context;
import com.example.spikedash_singleplayer.Skin;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SkinAdapter extends RecyclerView.Adapter<SkinAdapter.SkinViewHolder> {
    private List<Skin> skins;
    private Context context;

    public SkinAdapter(Context context, List<Skin> skins) {
        this.context = context;
        this.skins = skins;
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
