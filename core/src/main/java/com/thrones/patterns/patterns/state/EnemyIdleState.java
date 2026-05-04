package com.thrones.patterns.patterns.state;

import com.thrones.patterns.enemies.Enemy;

public class EnemyIdleState implements EnemyState {

    private float waitTimer = 0f;

    @Override
    public void update(Enemy enemy, float delta) {
        waitTimer += delta;
        if (enemy.getTarget() != null && waitTimer > 1f) {
            enemy.setState(new EnemyChaseState());
        }
    }

    @Override
    public String getStateName() { return "IDLE"; }
}
