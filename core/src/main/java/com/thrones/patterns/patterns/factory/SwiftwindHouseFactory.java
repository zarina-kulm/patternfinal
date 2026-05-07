package com.thrones.patterns.patterns.factory;

import com.thrones.patterns.characters.*;
import com.thrones.patterns.enemies.*;

public class SwiftwindHouseFactory implements HouseFactory {

    @Override
    public Hero createHero() { return new Archer("House Swiftwind"); }

    @Override
    public Enemy createEliteEnemy() { return new Dragon(); }

    @Override
    public String getHouseName() { return "House Swiftwind"; }

    @Override
    public String getHouseDescription() {
        return "Swift as wind, deadly as storm. Masters of ranged combat.";
    }
}
