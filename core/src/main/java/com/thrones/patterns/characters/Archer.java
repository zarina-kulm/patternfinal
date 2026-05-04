package com.thrones.patterns.characters;

public class Archer extends Hero {

    private boolean rainReady = true;
    private float cooldownTimer = 0f;
    private static final float COOLDOWN = 7f;
    private final float critChance = 0.25f;

    public Archer(String house) {
        super("Archer", house, 140f, 45f, 8f, 4f);
        this.specialAbilityName = "Rain of Arrows";
    }

    @Override
    public void useSpecialAbility() {
        if (rainReady) {
            System.out.println(name + " fires Rain of Arrows! DMG: " + (attack * 0.8f));
            rainReady = false;
            cooldownTimer = COOLDOWN;
        } else {
            System.out.println("Rain of Arrows cooldown: " + (int)cooldownTimer + "s");
        }
    }

    public float calculateDamage() {
        if (Math.random() < critChance) {
            System.out.println("CRITICAL HIT!");
            return attack * 2f;
        }
        return attack;
    }

    @Override
    public void update(float delta) {
        if (!rainReady) {
            cooldownTimer -= delta;
            if (cooldownTimer <= 0) rainReady = true;
        }
    }

    @Override
    public String getType() { return "ARCHER"; }

    public float getCritChance() { return critChance; }
}
