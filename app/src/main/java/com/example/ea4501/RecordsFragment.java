package com.example.ea4501;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordsFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordsFragment extends Fragment {
    private ListView recordsListView;
    private RecordsAdapter recordsAdapter;
    private ArrayList<GameRecord> recordsList;
    private GameDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);

        recordsListView = view.findViewById(R.id.recordsListView);
        recordsList = new ArrayList<>();
        recordsAdapter = new RecordsAdapter(getActivity(), recordsList);
        recordsListView.setAdapter(recordsAdapter);

        dbHelper = new GameDatabaseHelper(getActivity());
        loadRecords();

        return view;
    }

    private void loadRecords() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("GamesLog", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int gameId = cursor.getInt(cursor.getColumnIndexOrThrow("gameID"));
            String playerName = cursor.getString(cursor.getColumnIndexOrThrow("playerName"));
            String playDate = cursor.getString(cursor.getColumnIndexOrThrow("playDate"));
            int playTime = cursor.getInt(cursor.getColumnIndexOrThrow("playTime"));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
            int correctCount = cursor.getInt(cursor.getColumnIndexOrThrow("correctCount"));

            recordsList.add(new GameRecord(gameId, playerName, playDate, playTime, duration, correctCount));
        }

        cursor.close();
        recordsAdapter.notifyDataSetChanged();
    }
}