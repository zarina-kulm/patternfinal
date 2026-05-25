package com.thrones.patterns.patterns.factory;

import com.thrones.patterns.characters.*;
import com.thrones.patterns.enemies.*;

public class IronforgeHouseFactory implements HouseFactory {

    @Override
    public Hero createHero() { return new Knight("House Targaryen"); }

    @Override
    public Enemy createEliteEnemy() { return new DarkKnight(); }

    @Override
    public String getHouseName() { return "House Targaryen"; }

    @Override
    public String getHouseDescription() {
        return "Fire-born rulers with dragon blood. Strong melee warrior with royal armor.";
    }
}
