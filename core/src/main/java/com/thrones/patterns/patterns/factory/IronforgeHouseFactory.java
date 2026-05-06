package com.thrones.patterns.patterns.factory;

import com.thrones.patterns.characters.*;
import com.thrones.patterns.enemies.*;

public class IronforgeHouseFactory implements HouseFactory {

    @Override
    public Hero createHero() { return new Knight("House Ironforge"); }

    @Override
    public Enemy createEliteEnemy() { return new DarkKnight(); }

    @Override
    public String getHouseName() { return "House Ironforge"; }

    @Override
    public String getHouseDescription() {
        return "Masters of steel. Their knights are unbreakable.";
    }
}
