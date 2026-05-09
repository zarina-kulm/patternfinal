package com.thrones.patterns.patterns.decorator;

public class FireEnchantDecorator extends AbilityDecorator{
    private static final float BONUS=15f;
    public FireEnchantDecorator(Ability a) { 
        super(a); 
    }

    @Override
    public void execute(){
        wrapped.execute();
        System.out.println("  + Fire Enchant: +"+BONUS+" fire DMG!");
    }

    @Override
    public String getDescription(){
        return wrapped.getDescription()+" + Fire Enchant";
    }

    @Override
    public float getDamage() { 
        return wrapped.getDamage()+BONUS; 
    }
}