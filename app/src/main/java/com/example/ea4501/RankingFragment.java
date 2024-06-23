package com.example.ea4501;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankingFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankingFragment extends Fragment {
    private ListView rankingListView;
    private RankingAdapter rankingAdapter;
    private ArrayList<PlayerRanking> rankingList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        rankingListView = view.findViewById(R.id.rankingListView);
        rankingList = new ArrayList<>();
        rankingAdapter = new RankingAdapter(getActivity(), rankingList);
        rankingListView.setAdapter(rankingAdapter);

        new FetchRankingTask().execute("https://ranking-mobileasignment-wlicpnigvf.cn-hongkong.fcapp.run");

        return view;
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
                Toast.makeText(getActivity(), "Failed to parse JSON data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}