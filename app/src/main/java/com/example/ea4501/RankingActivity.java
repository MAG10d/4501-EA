package com.example.ea4501;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RankingActivity extends AppCompatActivity {

    private ListView rankingListView;
    private RankingAdapter rankingAdapter;
    private ArrayList<PlayerRanking> rankingList;
    private GameDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        rankingListView = findViewById(R.id.rankingListView);
        rankingList = new ArrayList<>();
        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingListView.setAdapter(rankingAdapter);

        dbHelper = new GameDatabaseHelper(this);

        new FetchRankingTask().execute("https://ranking-mobileasignment-wlicpnigvf.cn-hongkong.fcapp.run");
    }

    private class FetchRankingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                    reader.close();
                } else {
                    Log.e("FetchRankingTask", "HTTP error code: " + responseCode);
                    return null;
                }

                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e("FetchRankingTask", "Exception: " + e.getMessage(), e);
                return null;
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(RankingActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("Name");
                    int correct = jsonObject.getInt("Correct");
                    int time = jsonObject.getInt("Time");
                    rankingList.add(new PlayerRanking(name, correct, time));
                }

                // Add local records to the ranking list
                loadLocalRecords();

                rankingAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("FetchRankingTask", "JSON Parsing error: " + e.getMessage(), e);
                Toast.makeText(RankingActivity.this, "Failed to parse JSON data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadLocalRecords() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("GamesLog", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("playerName"));
            int correct = cursor.getInt(cursor.getColumnIndexOrThrow("correctCount"));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));

            rankingList.add(new PlayerRanking(name, correct, duration));
        }

        cursor.close();
    }
}