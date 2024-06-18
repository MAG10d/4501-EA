package com.example.ea4501;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RankingAdapter extends ArrayAdapter<PlayerRanking> {
    public RankingAdapter(Context context, ArrayList<PlayerRanking> rankings) {
        super(context, 0, rankings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlayerRanking ranking = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_ranking, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView correctTextView = convertView.findViewById(R.id.correctTextView);
        TextView timeTextView = convertView.findViewById(R.id.timeTextView);

        nameTextView.setText(ranking.getName());
        correctTextView.setText(String.valueOf(ranking.getCorrect()));
        timeTextView.setText(String.valueOf(ranking.getTime()));

        return convertView;
    }
}