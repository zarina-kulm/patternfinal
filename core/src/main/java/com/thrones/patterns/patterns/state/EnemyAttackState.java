package com.thrones.patterns.patterns.state;
import com.thrones.patterns.characters.Character;
import com.thrones.patterns.enemies.Enemy;

public class EnemyAttackState implements EnemyState {

    private float attackTimer = 0f;
    private static final float COOLDOWN = 1.5f;
    private static final float RANGE = 80f;

    @Override
    public void update(Enemy enemy, float delta) {
        if (!enemy.isAlive()) {
            enemy.setState(new EnemyDeadState());
            return;
        }
        Character target = enemy.getTarget();
        if (target == null || !target.isAlive()) {
            enemy.setState(new EnemyIdleState());
            return;
        }
        float dx = target.getX() - enemy.getX();
        float dy = target.getY() - enemy.getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > RANGE) {
            enemy.setState(new EnemyChaseState());
            return;
        }
        attackTimer += delta;
        if (attackTimer >= COOLDOWN) {
            enemy.performAttack(target);
            attackTimer = 0f;
        }
    }

    @Override
    public String getStateName() { return "ATTACK"; }
}
