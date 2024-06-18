package com.example.ea4501;

public class GameRecord {
    private int gameId;
    private String playDate;
    private int playTime;
    private int duration;
    private int correctCount;

    public GameRecord(int gameId, String playDate, int playTime, int duration, int correctCount) {
        this.gameId = gameId;
        this.playDate = playDate;
        this.playTime = playTime;
        this.duration = duration;
        this.correctCount = correctCount;
    }

    public int getGameId() {
        return gameId;
    }

    public String getPlayDate() {
        return playDate;
    }

    public int getPlayTime() {
        return playTime;
    }

    public int getDuration() {
        return duration;
    }

    public int getCorrectCount() {
        return correctCount;
    }
}