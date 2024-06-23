package com.example.ea4501;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {
    private ListView recordsListView;
    private RecordsAdapter recordsAdapter;
    private ArrayList<GameRecord> recordsList;
    private GameDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        recordsListView = findViewById(R.id.recordsListView);
        recordsList = new ArrayList<>();
        recordsAdapter = new RecordsAdapter(this, recordsList);
        recordsListView.setAdapter(recordsAdapter);

        dbHelper = new GameDatabaseHelper(this);
        loadRecords();
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