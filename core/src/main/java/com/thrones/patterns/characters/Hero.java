package com.thrones.patterns.characters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Hero extends Character {

    protected int level;
    protected int experience;
    protected String house;
    protected String specialAbilityName;

    public Hero(String name, String house, float hp,
                float attack, float defense, float speed) {
        super(name, hp, attack, defense, speed);
        this.house = house;
        this.level = 1;
        this.experience = 0;
    }
    public void gainExperience(int exp) {
        experience += exp;
        if (experience >= level * 100) levelUp();
    }
    protected void levelUp() {
        level++;
        maxHp += 20;
        hp = maxHp;
        attack += 5;
        defense += 2;
        experience = 0;
    }
    public abstract void useSpecialAbility();

    @Override
    public void update(float delta) {}

    @Override
    public void render(SpriteBatch batch) {}

    public int getLevel() { return level; }
    public String getHouse() { return house; }
    public String getSpecialAbilityName() { return specialAbilityName; }
}
