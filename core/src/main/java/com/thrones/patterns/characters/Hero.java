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

    // Баланс для трех героев (Jon, Daenerys, Arya)
    public enum HeroType {
        KNIGHT(200, 15, 3.0f),    // Джон Сноу: Высокий HP (200), средний урон (15), долгий откат ульты
        MAGE(100, 35, 5.0f),      // Дейенерис: Мало HP (100), огромный урон (35), очень долгий откат
        ARCHER(130, 20, 2.0f);    // Арья: Средний HP (130), частый урон (20), быстрый откат

        public final int maxHp;
        public final int baseDamage;
        public final float attackCooldown;

        HeroType(int maxHp, int baseDamage, float attackCooldown) {
            this.maxHp = maxHp;
            this.baseDamage = baseDamage;
            this.attackCooldown = attackCooldown;
        }
    }
}
