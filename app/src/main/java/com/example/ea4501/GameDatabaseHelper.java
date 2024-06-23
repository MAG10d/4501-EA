package com.example.ea4501;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "game.db";
    private static final int DATABASE_VERSION = 2; // Add user

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE GamesLog (" +
                "gameID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "playerName TEXT, " +
                "playDate TEXT, " +
                "playTime TEXT, " +
                "duration INTEGER, " +
                "correctCount INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE GamesLog ADD COLUMN playerName TEXT");
        }
    }
}