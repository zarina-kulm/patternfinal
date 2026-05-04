package com.thrones.patterns.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thrones.patterns.patterns.observer.GameEventObserver;
import com.thrones.patterns.patterns.observer.GameEventPublisher;
import com.thrones.patterns.patterns.observer.GameEventType;
import java.util.ArrayList;
import java.util.List;

public abstract class Character implements GameEventPublisher {

    protected String name;
    protected float hp, maxHp;
    protected float attack, defense, speed;
    protected float x, y;
    protected Texture texture;
    protected boolean alive;
    private final List<GameEventObserver> observers = new ArrayList<>();

    public Character(String name, float hp, float attack,
                     float defense, float speed) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.alive = true;
    }

    public void takeDamage(float damage) {
        float actual = Math.max(0, damage - defense);
        hp -= actual;
        notifyObservers(GameEventType.DAMAGE_TAKEN, this);
        if (hp <= 0) {
            hp = 0;
            alive = false;
            notifyObservers(GameEventType.CHARACTER_DIED, this);
        }
    }

    public void heal(float amount) {
        hp = Math.min(maxHp, hp + amount);
        notifyObservers(GameEventType.HEALED, this);
    }

    public abstract void update(float delta);
    public abstract void render(SpriteBatch batch);
    public abstract String getType();

    @Override
    public void addObserver(GameEventObserver o) { observers.add(o); }
    @Override
    public void removeObserver(GameEventObserver o) { observers.remove(o); }
    @Override
    public void notifyObservers(GameEventType type, Object data) {
        for (GameEventObserver o : observers) o.onEvent(type, data);
    }

    public String getName() { return name; }
    public float getHp() { return hp; }
    public float getMaxHp() { return maxHp; }
    public float getAttack() { return attack; }
    public float getDefense() { return defense; }
    public float getSpeed() { return speed; }
    public float getX() { return x; }
    public float getY() { return y; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public boolean isAlive() { return alive; }
    public void setTexture(Texture t) { this.texture = t; }
}
