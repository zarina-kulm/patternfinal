package com.thrones.patterns.enemies;

import com.thrones.patterns.characters.Character;

public class DarkKnight extends Enemy{
    private boolean blockActive=true;
    private float blockCooldown=0f;
    public DarkKnight(){
        super("Dark Knight", 180f, 45f, 25f, 2f, 50, 80);
    }

    @Override
    public void performAttack(Character target) {
        System.out.println("Dark Knight strikes "+target.getName()+" for "+attack);
        target.takeDamage(attack);
    }

    @Override
    public void takeDamage(float damage){
        if(blockActive){
            System.out.println("Dark Knight BLOCKS!");
            blockActive=false;
            blockCooldown=5f;
            return;
        }
        super.takeDamage(damage);
    }

    @Override
    public void update(float delta){
        super.update(delta);
        if(blockCooldown>0){
            blockCooldown-=delta;
            if(blockCooldown<=0) blockActive=true;
        }
    }

    @Override
    public String getType() {
        return "DARK_KNIGHT";
    }
}
