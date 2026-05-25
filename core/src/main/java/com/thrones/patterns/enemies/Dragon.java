package com.thrones.patterns.enemies;

import com.thrones.patterns.characters.Character;

public class Dragon extends Enemy{

    private float fireCooldown=0f;
    private boolean enraged=false;

    public Dragon(){
        super("Ancient Dragon", 500f, 70f, 30f, 1f, 200, 300);
    }

    @Override
    public void performAttack(Character target){
        if(fireCooldown<=0){
            float dmg=attack*2f;
            System.out.println("Dragon breathes FIRE on "+target.getName()+" for "+dmg+"!");
            target.takeDamage(dmg);
            fireCooldown=15f;
        } else {
            float dmg=attack*(enraged ? 1.5f : 1f);
            System.out.println("Dragon claws "+target.getName()+" for "+dmg);
            target.takeDamage(dmg);
        }
    }

    @Override
    public void update(float delta){
        super.update(delta);
        if(fireCooldown>0) fireCooldown-=delta;
        enraged=hp<(maxHp*0.25f);
    }

    @Override
    public String getType() {
        return "DRAGON";
    }

    public boolean isEnraged() {
        return enraged;
    }
}
