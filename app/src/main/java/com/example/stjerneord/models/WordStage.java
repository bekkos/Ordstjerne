package com.example.stjerneord.models;

import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class WordStage {
    private String letters;
    private Character mainLetter;
    private List<String> words;
    private List<String> discoveredWords;
    int score = 0;
    int maxScore;

    public WordStage(String[] stageData) {
        this.letters = stageData[0];
        this.mainLetter = letters.charAt(3);
        this.words = new ArrayList<>();
        this.discoveredWords = new ArrayList<>();

        for(int i = 1; i < stageData.length; i++) {
            words.add(stageData[i]);
        }
        this.maxScore = words.size();

        System.out.println(words.toString());
    }

    public void addDiscoveredWord(String word) {
        discoveredWords.add(word);
    }

    public List<String> getDiscoveredWords() {
        return discoveredWords;
    }

    public void clearDiscoveredWords() {
        discoveredWords.clear();
    }

    public String getLetters() {
        return this.letters;
    }

    public List<String> getWords() {
        return this.words;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public Character getMainLetter() {
        return mainLetter;
    }


}
