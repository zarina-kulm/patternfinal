package com.thrones.patterns.patterns.factory;

import com.thrones.patterns.characters.Hero;
import com.thrones.patterns.enemies.Enemy;

public interface HouseFactory {
    Hero createHero();
    Enemy createEliteEnemy();
    String getHouseName();
    String getHouseDescription();
}
