package com.thrones.patterns.patterns.factory;

import com.thrones.patterns.characters.*;
import com.thrones.patterns.enemies.*;

public class ShadowmereHouseFactory implements HouseFactory {

    @Override
    public Hero createHero() { return new Mage("House Shadowmere"); }

    @Override
    public Enemy createEliteEnemy() { return new Necromancer(); }

    @Override
    public String getHouseName() { return "House Shadowmere"; }

    @Override
    public String getHouseDescription() {
        return "Wielders of dark magic. Their power bends reality.";
    }
}
