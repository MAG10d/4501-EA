package com.example.ea4501;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        rankingListView = findViewById(R.id.rankingListView);
        rankingList = new ArrayList<>();
        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingListView.setAdapter(rankingAdapter);

        new FetchRankingTask().execute("https://ranking-mobileasignment-wlicpnigvf.cn-hongkong.fcapp.run");
    }

    private class FetchRankingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("Name");
                    int correct = jsonObject.getInt("Correct");
                    int time = jsonObject.getInt("Time");
                    rankingList.add(new PlayerRanking(name, correct, time));
                }

                rankingAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(RankingActivity.this, "Failed to parse JSON data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}