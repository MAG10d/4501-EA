package com.example.ea4501;

public class PlayerRanking {
    private String name;
    private int correct;
    private int time;

    public PlayerRanking(String name, int correct, int time) {
        this.name = name;
        this.correct = correct;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public int getCorrect() {
        return correct;
    }

    public int getTime() {
        return time;
    }
}