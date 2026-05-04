package com.thrones.patterns.patterns.observer;

import com.thrones.patterns.characters.Character;

public class BattleEventLogger implements GameEventObserver {

    @Override
    public void onEvent(GameEventType type, Object data) {
        String name = (data instanceof Character)
            ? ((Character) data).getName() : data.toString();

        switch (type) {
            case DAMAGE_TAKEN:
                System.out.println("[LOG] " + name + " took damage! HP: " +
                    (data instanceof Character ? (int)((Character)data).getHp() : "?"));
                break;
            case CHARACTER_DIED:
                System.out.println("[LOG] " + name + " has DIED!");
                break;
            case HEALED:
                System.out.println("[LOG] " + name + " was healed!");
                break;
            case LEVEL_UP:
                System.out.println("[LOG] " + name + " LEVELED UP!");
                break;
            case WAVE_STARTED:
                System.out.println("[LOG] Wave started: " + data);
                break;
            case WAVE_COMPLETED:
                System.out.println("[LOG] Wave completed!");
                break;
            case BOSS_SPAWNED:
                System.out.println("[LOG] !! BOSS: " + name + " !!");
                break;
            case GOLD_EARNED:
                System.out.println("[LOG] Gold earned: " + data);
                break;
            case ABILITY_USED:
                System.out.println("[LOG] Ability used by " + name);
                break;
        }
    }
}
