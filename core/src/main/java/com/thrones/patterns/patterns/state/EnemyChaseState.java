package com.thrones.patterns.patterns.state;
import com.thrones.patterns.characters.Character;
import com.thrones.patterns.enemies.Enemy;

public class EnemyChaseState implements EnemyState {
    private static final float ATTACK_RANGE = 80f;
    @Override
    public void update(Enemy enemy, float delta) {
        Character target = enemy.getTarget();
        if (target == null || !target.isAlive()) {
            enemy.setState(new EnemyIdleState());
            return;
        }
        float dx = target.getX() - enemy.getX();
        float dy = target.getY() - enemy.getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist < ATTACK_RANGE) {
            enemy.setState(new EnemyAttackState());
        } else {
            enemy.setPosition(
                enemy.getX() + (dx / dist) * enemy.getSpeed() * delta,
                enemy.getY() + (dy / dist) * enemy.getSpeed() * delta
            );
        }
    }

    @Override
    public String getStateName() { return "CHASE"; }
}
