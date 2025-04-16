package com.example.spikedash_singleplayer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class PlayerStatsAdapter extends ArrayAdapter<PlayerStats> {

    Context context;
    List<PlayerStats> statsList;
    boolean showGames;
    String loggedInUid;



    public PlayerStatsAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<PlayerStats> statsList, boolean showGames, String loggedInUid) {
        super(context, resource, textViewResourceId, statsList);
        this.context = context;
        this.statsList = statsList;
        this.showGames = showGames;
        this.loggedInUid = loggedInUid;
    }

    @NonNull @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.stats_layout,parent, false);
        TextView tvName = v.findViewById(R.id.tvName);
        TextView tvValue = v.findViewById(R.id.tvValue);
        TextView tvRank = v.findViewById(R.id.tvRank);
        RelativeLayout root = v.findViewById(R.id.itemRoot);


        PlayerStats temp = statsList.get(position);
        tvName.setText(temp.getUsername());
        tvRank.setText((position + 1) + ".");


        if (showGames) {
            tvValue.setText(String.valueOf(temp.getGames()));
        } else {
            tvValue.setText(String.valueOf(temp.getHighScore()));
        }

        if (temp.getUid() != null && temp.getUid().equals(loggedInUid)) {
            root.setBackgroundColor(context.getResources().getColor(R.color.highlight_yellow));
        }



        return v;
    }
}
