package com.thrones.patterns.patterns.decorator;

public class PoisonCoatDecorator extends AbilityDecorator{
    private static final float DPS=5f;
    public PoisonCoatDecorator(Ability a) { 
        super(a); 
    }

    @Override
    public void execute(){
        wrapped.execute();
        System.out.println("  + Poison Coat: "+DPS+" DPS for 3s!");
    }

    @Override
    public String getDescription(){
        return wrapped.getDescription()+" + Poison Coat";
    }

    @Override
    public float getDamage(){ 
        return wrapped.getDamage()+DPS*3f; 
    }
}