package com.thrones.patterns.characters;

public class Mage extends Hero {

    private boolean arcaneNovaReady = true;
    private float cooldownTimer = 0f;
    private static final float COOLDOWN = 10f;
    private float mana, maxMana;

    public Mage(String house) {
        super("Mage", house, 120f, 60f, 5f, 3f);
        this.specialAbilityName = "Arcane Nova";
        this.maxMana = 100f;
        this.mana = maxMana;
    }

    @Override
    public void useSpecialAbility() {
        if (arcaneNovaReady && mana >= 40f) {
            System.out.println(name + " casts Arcane Nova! AoE: " + (attack * 1.5f));
            mana -= 40f;
            arcaneNovaReady = false;
            cooldownTimer = COOLDOWN;
        } else if (mana < 40f) {
            System.out.println("Not enough mana!");
        } else {
            System.out.println("Arcane Nova cooldown: " + (int)cooldownTimer + "s");
        }
    }

    @Override
    public void update(float delta) {
        mana = Math.min(maxMana, mana + 5f * delta);
        if (!arcaneNovaReady) {
            cooldownTimer -= delta;
            if (cooldownTimer <= 0) arcaneNovaReady = true;
        }
    }

    @Override
    public String getType() { return "MAGE"; }

    public float getMana() { return mana; }
    public float getMaxMana() { return maxMana; }
}
