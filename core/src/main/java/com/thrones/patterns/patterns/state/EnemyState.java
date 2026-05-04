package com.thrones.patterns.patterns.state;

import com.thrones.patterns.enemies.Enemy;

public interface EnemyState {
    void update(Enemy enemy, float delta);
    String getStateName();
}
