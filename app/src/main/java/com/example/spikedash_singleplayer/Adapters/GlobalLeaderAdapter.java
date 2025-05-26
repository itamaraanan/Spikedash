package com.example.spikedash_singleplayer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spikedash_singleplayer.PlayerStats;
import com.example.spikedash_singleplayer.R;

import java.util.List;
public class GlobalLeaderAdapter extends RecyclerView.Adapter<GlobalLeaderAdapter.ViewHolder> {

    private Context context;
    private List<PlayerStats> statsList;
    private String loggedInUid;

    public GlobalLeaderAdapter(Context context, List<PlayerStats> statsList, String loggedInUid) {
        //constructor
        this.context = context;
        this.statsList = statsList;
        this.loggedInUid = loggedInUid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View v = LayoutInflater.from(context).inflate(R.layout.stats_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerStats temp = statsList.get(position);
        // Set the username, rank, and high score
        holder.tvName.setText(temp.getUsername());
        holder.tvRank.setText((position + 1) + ".");
        holder.tvValue.setText(String.valueOf(temp.getHighScore()));
        // Highlight the item if it belongs to the logged-in user
        if (temp.getUid() != null && temp.getUid().equals(loggedInUid)) {
            holder.root.setBackgroundColor(context.getResources().getColor(R.color.highlight_yellow));
        } else {
            holder.root.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }
    }

    @Override
    public int getItemCount() {
        return statsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvValue, tvRank;
        RelativeLayout root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvValue = itemView.findViewById(R.id.tvValue);
            tvRank = itemView.findViewById(R.id.tvRank);
            root = itemView.findViewById(R.id.itemRoot);
        }
    }
}
