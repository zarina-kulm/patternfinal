package com.thrones.patterns.patterns.decorator;

public class BasicAttack implements Ability{
    private final float baseDamage;

    public BasicAttack(float baseDamage){ 
        this.baseDamage=baseDamage; 
    }

    @Override
    public void execute(){
        System.out.println("Basic Attack! DMG: "+baseDamage);
    }

    @Override
    public String getDescription(){ 
        return "Basic Attack"; 
    }

    @Override
    public float getDamage(){ 
        return baseDamage; 
    }
}