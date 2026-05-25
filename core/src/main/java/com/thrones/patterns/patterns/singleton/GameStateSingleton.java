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

    private GameStateSingleton() {
        init();
    }

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

    public void reset() {
        init();
    }

    public void addScore(int points) {
        score += points;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    public void nextLevel() {
        level++;

        if (level > 9) {
            level = 9;
            victory = true;
        }

        wave = level;
    }

    public void setLevel(int level) {
        this.level = level;

        if (this.level < 1) {
            this.level = 1;
        }

        if (this.level > 9) {
            this.level = 9;
        }

        this.wave = this.level;
    }

    public void nextWave() {
        nextLevel();
    }

    public int getScore() {
        return score;
    }

    public int getGold() {
        return gold;
    }

    public int getLevel() {
        return level;
    }

    public int getWave() {
        return wave;
    }

    public String getSelectedHeroType() {
        return selectedHeroType;
    }

    public void setSelectedHeroType(String type) {
        if (type == null || type.trim().isEmpty()) {
            selectedHeroType = "KNIGHT";
        } else {
            selectedHeroType = type;
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean v) {
        gameOver = v;
    }

    public boolean isVictory() {
        return victory;
    }

    public void setVictory(boolean v) {
        victory = v;
    }
}
