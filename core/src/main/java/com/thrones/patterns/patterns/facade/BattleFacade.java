package com.thrones.patterns.patterns.facade;

import com.badlogic.gdx.Gdx;
import com.thrones.patterns.characters.Hero;
import com.thrones.patterns.enemies.Enemy;
import com.thrones.patterns.patterns.builder.BattleConfig;
import com.thrones.patterns.patterns.observer.BattleEventLogger;
import com.thrones.patterns.patterns.singleton.GameStateSingleton;
import java.util.List;

public class BattleFacade {

    private final BattleEventLogger logger;
    private final GameStateSingleton gameState;

    private float heroAttackTimer = 0f;
    private final float HERO_ATTACK_COOLDOWN = 0.8f;

    public BattleFacade() {
        this.logger = new BattleEventLogger();
        this.gameState = GameStateSingleton.getInstance();
    }

    public void setupBattle(BattleConfig config) {
        config.getHero().addObserver(logger);
        for (Enemy e : config.getEnemies()) {
            e.addObserver(logger);
            e.setTarget(config.getHero());
        }
        System.out.println("=== " + config.getBattleName() + " START ===");
    }

    public void heroAttacks(Hero hero, Enemy enemy) {

        if (heroAttackTimer > 0) {
            return;
        }

        enemy.takeDamage(hero.getAttack());
        heroAttackTimer = HERO_ATTACK_COOLDOWN;
        if (!enemy.isAlive()) {
            gameState.addGold(enemy.getGoldReward());
            gameState.addScore(enemy.getExpReward() * 10);
            System.out.println("+" + enemy.getGoldReward() + " gold!");
        }
    }

    public void enemyAttacks(Enemy enemy, Hero hero) {
        if (enemy.isAlive()) enemy.performAttack(hero);
    }

    public boolean isBattleOver(Hero hero, List<Enemy> enemies) {
        if (heroAttackTimer > 0) {
            heroAttackTimer -= Gdx.graphics.getDeltaTime();
        }
        if (!hero.isAlive()) {
            gameState.setGameOver(true);
            return true;
        }
        if (enemies.stream().noneMatch(Enemy::isAlive)) {
            gameState.setVictory(true);
            return true;
        }
        return false;
    }

}
