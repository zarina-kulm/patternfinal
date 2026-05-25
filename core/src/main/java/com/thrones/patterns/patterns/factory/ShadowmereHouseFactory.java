package com.thrones.patterns.patterns.factory;

import com.thrones.patterns.characters.*;
import com.thrones.patterns.enemies.*;

public class ShadowmereHouseFactory implements HouseFactory {

    @Override
    public Hero createHero() { return new Mage("House Lannister"); }

    @Override
    public Enemy createEliteEnemy() { return new Necromancer(); }

    @Override
    public String getHouseName() { return "House Lannister"; }

    @Override
    public String getHouseDescription() {
        return "A rich and dangerous house. Uses gold, politics and forbidden magic.";
    }
}
