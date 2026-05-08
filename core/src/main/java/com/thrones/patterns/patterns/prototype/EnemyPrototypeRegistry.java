package com.thrones.patterns.patterns.prototype;

import com.thrones.patterns.enemies.DarkKnight;
import com.thrones.patterns.enemies.Dragon;
import com.thrones.patterns.enemies.Enemy;
import com.thrones.patterns.enemies.Goblin;
import com.thrones.patterns.enemies.Necromancer;
import com.thrones.patterns.enemies.Orc;
public class EnemyPrototypeRegistry{

    private static EnemyPrototypeRegistry instance;
 
    private EnemyPrototypeRegistry(){}

    public static EnemyPrototypeRegistry getInstance() {
        if(instance==null) instance=new EnemyPrototypeRegistry();
        return instance;
    }

    public Enemy spawn(String key){
        switch (key.toUpperCase()){
            case "GOBLIN":      return new Goblin();
            case "ORC":         return new Orc();
            case "DARK_KNIGHT": return new DarkKnight();
            case "NECROMANCER": return new Necromancer();
            case "DRAGON":      return new Dragon();
            default: throw new IllegalArgumentException("No prototype: "+key);
        }
    }
}