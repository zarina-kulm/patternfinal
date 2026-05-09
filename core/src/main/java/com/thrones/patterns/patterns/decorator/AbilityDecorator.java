package com.thrones.patterns.patterns.decorator;

public abstract class AbilityDecorator implements Ability{
    protected final Ability wrapped;
    public AbilityDecorator(Ability ability){ 
        this.wrapped=ability; 
    }
}