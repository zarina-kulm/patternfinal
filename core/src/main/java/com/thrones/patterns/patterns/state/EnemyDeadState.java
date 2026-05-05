package com.thrones.patterns.patterns.state;

import com.thrones.patterns.enemies.Enemy;

public class EnemyDeadState implements EnemyState {

    private float timer = 0f;

    @Override
    public void update(Enemy enemy, float delta) {
        timer += delta;
        if (timer >= 1f) {
            System.out.println(enemy.getName() + " fades away...");
        }
    }

    @Override
    public String getStateName() { return "DEAD"; }
    public boolean isDone() { return timer >= 1f; }
}
