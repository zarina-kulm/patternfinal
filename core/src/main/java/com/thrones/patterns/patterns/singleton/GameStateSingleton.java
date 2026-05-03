package com.thrones.patterns.patterns.singleton;

public class GameStateSingleton {

    private static GameStateSingleton instance;

    private int score;
    private int gold;
    private int level;
    private int wave;
    private String selectedHeroType;
    private boolean gameOver;
    private boolean victory;

    private GameStateSingleton() {}

    public static GameStateSingleton getInstance() {
        if (instance == null) {
            instance = new GameStateSingleton();
        }
        return instance;
    }

    public void init() {
        score = 0;
        gold = 100;
        level = 1;
        wave = 1;
        selectedHeroType = "KNIGHT";
        gameOver = false;
        victory = false;
    }

    public void reset() { init(); }

    public void addScore(int points) { score += points; }
    public void addGold(int amount) { gold += amount; }
    public boolean spendGold(int amount) {
        if (gold >= amount) { gold -= amount; return true; }
        return false;
    }
    public void nextWave() { wave++; }

    public int getScore() { return score; }
    public int getGold() { return gold; }
    public int getLevel() { return level; }
    public int getWave() { return wave; }
    public String getSelectedHeroType() { return selectedHeroType; }
    public void setSelectedHeroType(String type) { selectedHeroType = type; }
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean v) { gameOver = v; }
    public boolean isVictory() { return victory; }
    public void setVictory(boolean v) { victory = v; }
}
