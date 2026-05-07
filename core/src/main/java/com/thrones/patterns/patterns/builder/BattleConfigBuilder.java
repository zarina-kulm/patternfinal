package com.thrones.patterns.patterns.builder;

import com.thrones.patterns.characters.Hero;
import com.thrones.patterns.enemies.Enemy;
import java.util.ArrayList;
import java.util.List;

public class BattleConfigBuilder {

    private String battleName = "Battle";
    private Hero hero;
    private List<Enemy> enemies = new ArrayList<>();
    private int waveCount = 1;
    private boolean hasBoss = false;
    private int goldMultiplier = 1;

    public BattleConfigBuilder setBattleName(String n) {
        battleName = n; return this; }
    public BattleConfigBuilder setHero(Hero h) {
        hero = h; return this; }
    public BattleConfigBuilder addEnemy(Enemy e) {
        enemies.add(e); return this; }
    public BattleConfigBuilder setWaveCount(int w) {
        waveCount = w; return this; }
    public BattleConfigBuilder withBoss() {
        hasBoss = true; return this; }
    public BattleConfigBuilder setGoldMultiplier(int m) {
        goldMultiplier = m; return this;
    }

    public BattleConfig build() {
        return new BattleConfig(battleName, hero, enemies,
            waveCount, hasBoss, goldMultiplier);
    }
}
