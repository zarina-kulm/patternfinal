package com.thrones.patterns.patterns.prototype;

import com.thrones.patterns.enemies.DarkKnight;
import com.thrones.patterns.enemies.Dragon;
import com.thrones.patterns.enemies.Enemy;
import com.thrones.patterns.enemies.Goblin;
import com.thrones.patterns.enemies.Necromancer;
import com.thrones.patterns.enemies.Orc;

public class EnemyPrototypeRegistry {

    private static EnemyPrototypeRegistry instance;

    private EnemyPrototypeRegistry() {}

    public static EnemyPrototypeRegistry getInstance() {
        if (instance == null) {
            instance = new EnemyPrototypeRegistry();
        }
        return instance;
    }

    public Enemy spawn(String key) {
        Enemy enemy;

        // 1. Создаем объект врага в зависимости от ключа
        switch (key.toUpperCase()) {
            case "GOBLIN":
                enemy = new Goblin();
                // Настраиваем баланс для Wight (Обычный зомби)
                enemy.setName("Wight (Зомби)");
                enemy.setHealth(40);
                enemy.setMaxHealth(40);
                enemy.setDamage(5); // Теперь он бьет слабо, не убьет сразу
                break;

            case "ORC":
                enemy = new Orc();
                // Настраиваем баланс для White Walker (Белый Ходок)
                enemy.setName("White Walker");
                enemy.setHealth(90);
                enemy.setMaxHealth(90);
                enemy.setDamage(12);
                break;

            case "DARK_KNIGHT":
                enemy = new DarkKnight();
                // Настраиваем баланс для Undead Giant (Зомби-Великан)
                enemy.setName("Undead Giant");
                enemy.setHealth(250);
                enemy.setMaxHealth(250);
                enemy.setDamage(22); // Сильный, но медленный босс подземелья
                break;

            case "NECROMANCER":
                enemy = new Necromancer();
                // Настраиваем баланс для Night King (Король Ночи)
                enemy.setName("Night King");
                enemy.setHealth(400);
                enemy.setMaxHealth(400);
                enemy.setDamage(30);
                break;

            case "DRAGON":
                enemy = new Dragon();
                // Настраиваем баланс для Ice Dragon (Ледяной Дракон Визерион)
                enemy.setName("Ice Dragon Viserion");
                enemy.setHealth(600);
                enemy.setMaxHealth(600);
                enemy.setDamage(45); // Финальный супер-босс 5-й волны
                break;

            default:
                throw new IllegalArgumentException("No prototype found for key: " + key);
        }

        return enemy;
    }
}
