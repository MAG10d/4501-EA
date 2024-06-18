package com.example.ea4501;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class RecordsAdapter extends ArrayAdapter<GameRecord> {
    public RecordsAdapter(Context context, ArrayList<GameRecord> records) {
        super(context, 0, records);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GameRecord record = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_record, parent, false);
        }

        TextView playDateTextView = convertView.findViewById(R.id.playDateTextView);
        TextView playTimeTextView = convertView.findViewById(R.id.playTimeTextView);
        TextView durationTextView = convertView.findViewById(R.id.durationTextView);
        TextView correctCountTextView = convertView.findViewById(R.id.correctCountTextView);

        playDateTextView.setText(record.getPlayDate());
        playTimeTextView.setText(String.valueOf(record.getPlayTime()));
        durationTextView.setText(String.valueOf(record.getDuration()));
        correctCountTextView.setText(String.valueOf(record.getCorrectCount()));

        return convertView;
    }
}