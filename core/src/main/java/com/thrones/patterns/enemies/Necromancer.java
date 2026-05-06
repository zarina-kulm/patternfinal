package com.thrones.patterns.enemies;

import com.thrones.patterns.characters.Character;

public class Necromancer extends Enemy{

    private int summonCount=0;
    private float summonCooldown=12f;

    public Necromancer(){
        super("Necromancer", 150f, 40f, 8f, 1.5f, 80, 120);
    }

    @Override
    public void performAttack(Character target){
        System.out.println("Necromancer casts Death Bolt on "+target.getName()+" for "+attack);
        target.takeDamage(attack);
    }

    @Override
    public void update(float delta){
        super.update(delta);
        summonCooldown-=delta;
        if(summonCooldown<=0 && summonCount<3){
            summonCount++;
            System.out.println("Necromancer summons undead! (" +summonCount+ "/3)");
            summonCooldown=12f;
        }
    }

    @Override
    public String getType() { 
        return "NECROMANCER"; 
    }
}
