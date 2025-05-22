package com.example.spikedash_singleplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class FriendsLeaderAdapter extends RecyclerView.Adapter<FriendsLeaderAdapter.ViewHolder> {

    private Context context;
    private List<PlayerStats> statsList;
    private boolean showGames;
    private String loggedInUid;

    public FriendsLeaderAdapter(Context context, List<PlayerStats> statsList, boolean showGames, String loggedInUid) {
        this.context = context;
        this.statsList = statsList;
        this.showGames = showGames;
        this.loggedInUid = loggedInUid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.stats_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerStats temp = statsList.get(position);

        holder.tvName.setText(temp.getUsername());
        holder.tvRank.setText((position + 1) + ".");
        holder.tvValue.setText(showGames ? String.valueOf(temp.getGames()) : String.valueOf(temp.getHighScore()));

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
