package com.thrones.patterns.enemies;

import com.thrones.patterns.characters.Character;

public class Orc extends Enemy{
    private boolean rageActive=false;
    public Orc(){
        super("Orc", 120f, 35f, 10f, 2.5f, 25, 40);
    }

    @Override
    public void performAttack(Character target){
        float dmg=rageActive ? attack * 1.5f : attack;
        System.out.println("Orc smashes "+target.getName()+" for "+dmg+(rageActive ? " (RAGE!)" : ""));
        target.takeDamage(dmg);
    }

    @Override
    public void update(float delta){
        super.update(delta);
        rageActive=hp<(maxHp*0.3f);
    }

    @Override
    public String getType() { 
        return "ORC"; 
    }

    public boolean isRageActive() { 
        return rageActive; 
    }
}
