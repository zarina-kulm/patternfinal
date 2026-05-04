package com.thrones.patterns.characters;

public class Knight extends Hero {

    private boolean shieldBashReady = true;
    private float cooldownTimer = 0f;
    private static final float COOLDOWN = 8f;

    public Knight(String house) {
        super("Knight", house, 200f, 30f, 20f, 2.5f);
        this.specialAbilityName = "Shield Bash";
    }
    @Override
    public void useSpecialAbility() {
        if (shieldBashReady) {
            System.out.println(name + " uses Shield Bash! DMG: " + (attack * 2f));
            shieldBashReady = false;
            cooldownTimer = COOLDOWN;
        } else {
            System.out.println("Shield Bash cooldown: " + (int)cooldownTimer + "s");
        }
    }
    @Override
    public void update(float delta) {
        if (!shieldBashReady) {
            cooldownTimer -= delta;
            if (cooldownTimer <= 0) shieldBashReady = true;
        }
    }
    @Override
    public String getType() { return "KNIGHT"; }
    public boolean isShieldBashReady() { return shieldBashReady; }
}
