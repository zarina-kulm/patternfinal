package com.thrones.patterns.patterns.builder;

import com.thrones.patterns.characters.Hero;
import com.thrones.patterns.enemies.Enemy;
import java.util.List;

public class BattleConfig {

    private final String battleName;
    private final Hero hero;
    private final List<Enemy> enemies;
    private final int waveCount;
    private final boolean hasBoss;
    private final int goldMultiplier;

    public BattleConfig(String battleName, Hero hero, List<Enemy> enemies,
                        int waveCount, boolean hasBoss, int goldMultiplier) {
        this.battleName = battleName;
        this.hero = hero;
        this.enemies = enemies;
        this.waveCount = waveCount;
        this.hasBoss = hasBoss;
        this.goldMultiplier = goldMultiplier;
    }

    public String getBattleName() { return battleName; }
    public Hero getHero() { return hero; }
    public List<Enemy> getEnemies() { return enemies; }
    public int getWaveCount() { return waveCount; }
    public boolean hasBoss() { return hasBoss; }
    public int getGoldMultiplier() { return goldMultiplier; }
}
