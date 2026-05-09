package com.thrones.patterns.patterns.decorator;

public class BlessedStrikeDecorator extends AbilityDecorator{
    private static final float HOLY=25f;
    public BlessedStrikeDecorator(Ability a) { 
        super(a); }

    @Override
    public void execute(){
        wrapped.execute();
        System.out.println("  + Blessed Strike: +"+HOLY+" holy DMG!");
    }

    @Override
    public String getDescription(){
        return wrapped.getDescription()+" + Blessed Strike";
    }

    @Override
    public float getDamage(){ 
        return wrapped.getDamage()+HOLY; 
    }
}