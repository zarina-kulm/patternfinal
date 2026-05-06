package com.thrones.patterns.patterns.factory;
import com.thrones.patterns.characters.*;

public class HeroFactory {

    public static Hero createHero(String type, String house) {
        switch (type.toUpperCase()) {
            case "KNIGHT": return new Knight(house);
            case "MAGE":   return new Mage(house);
            case "ARCHER": return new Archer(house);
            default: throw new IllegalArgumentException("Unknown: " + type);
        }
    }
}
