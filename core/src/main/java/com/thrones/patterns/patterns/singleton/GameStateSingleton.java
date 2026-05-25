package com.thrones.patterns.patterns.singleton;

public class GameStateSingleton {

    private static GameStateSingleton instance;

    public static final int MAX_LEVEL = 7;
    public static final int PART_TWO_START_LEVEL = 5;

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

        if (level > MAX_LEVEL) {
            level = MAX_LEVEL;
            victory = true;
        }

        wave = level;
    }

    public void setLevel(int level) {
        this.level = level;

        if (this.level < 1) {
            this.level = 1;
        }

        if (this.level > MAX_LEVEL) {
            this.level = MAX_LEVEL;
        }

        this.wave = this.level;
    }

    public void nextWave() {
        nextLevel();
    }

    public boolean isPartOne() {
        return level < PART_TWO_START_LEVEL;
    }

    public boolean isPartTwo() {
        return level >= PART_TWO_START_LEVEL;
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
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
