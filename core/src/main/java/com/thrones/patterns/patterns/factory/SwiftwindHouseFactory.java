package com.thrones.patterns.patterns.factory;

import com.thrones.patterns.characters.*;
import com.thrones.patterns.enemies.*;

public class SwiftwindHouseFactory implements HouseFactory {

    @Override
    public Hero createHero() { return new Archer("House Stark"); }

    @Override
    public Enemy createEliteEnemy() { return new Dragon(); }

    @Override
    public String getHouseName() { return "House Stark"; }

    @Override
    public String getHouseDescription() {
        return "A tactical house of survivors, hunters and long-range assassins.";
    }
}
