package com.thrones.patterns.enemies;

import com.thrones.patterns.characters.Character;

public class Goblin extends Enemy{
    public Goblin(){
        super("Goblin", 50f, 15f, 2f, 5f, 10, 20);
    }

    @Override
    public void performAttack(Character target){
        System.out.println("Goblin slashes "+target.getName()+" for "+attack);
        target.takeDamage(attack);
    }

    @Override
    public String getType(){ 
        return "GOBLIN"; 
    } 
}
