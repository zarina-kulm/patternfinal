package com.thrones.patterns.enemies;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thrones.patterns.characters.Character;
import com.thrones.patterns.patterns.state.EnemyIdleState;
import com.thrones.patterns.patterns.state.EnemyState;

public abstract class Enemy extends Character {

    protected int goldReward;
    protected int expReward;
    protected EnemyState currentState;
    protected Character target;

    public Enemy(String name, float hp, float attack, float defense,
                 float speed, int goldReward, int expReward) {
        super(name, hp, attack, defense, speed);
        this.goldReward = goldReward;
        this.expReward = expReward;
        this.currentState = new EnemyIdleState();
    }

    public void setState(EnemyState state) { this.currentState = state; }
    public void setTarget(Character target) { this.target = target; }

    @Override
    public void update(float delta) {
        if (currentState != null && alive)
            currentState.update(this, delta);
    }

    @Override
    public void render(SpriteBatch batch) {}

    public abstract void performAttack(Character target);

    public int getGoldReward() { return goldReward; }
    public int getExpReward() { return expReward; }
    public Character getTarget() { return target; }
    public EnemyState getCurrentState() { return currentState; }

    public void setName(String name) {
        this.name = name;
    }

    public void setHealth(float health) {
        this.hp = health;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHp = maxHealth;
    }

    public void setDamage(float damage) {
        this.attack = damage;
    }
}
