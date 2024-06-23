package com.example.ea4501;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText playerNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerNameInput = findViewById(R.id.playerNameInput);
    }

    public void startGame(View view) {
        String playerName = playerNameInput.getText().toString().trim();
        if (playerName.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("playerName", playerName);
        startActivity(intent);
    }

    public void showRanking(View view) {
        Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }

    public void showRecords(View view) {
        Intent intent = new Intent(this, RecordsActivity.class);
        startActivity(intent);
    }

    public void closeApp(View view) {
        finish();
    }
}