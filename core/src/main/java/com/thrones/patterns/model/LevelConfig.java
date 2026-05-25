package com.thrones.patterns.model;

public class LevelConfig {

    public final int level;
    public final String title;
    public final String objective;
    public final String[] enemies;
    public final String background;
    public final boolean bossLevel;

    public LevelConfig(
        int level,
        String title,
        String objective,
        String[] enemies,
        String background,
        boolean bossLevel
    ) {
        this.level = level;
        this.title = title;
        this.objective = objective;
        this.enemies = enemies;
        this.background = background;
        this.bossLevel = bossLevel;
    }

    public static LevelConfig getLevel(int level) {
        switch (level) {
            case 1:
                return new LevelConfig(
                    1,
                    "Ruins of Winterhold",
                    "Defeat the first goblin scouts.",
                    new String[]{"GOBLIN", "GOBLIN"},
                    "ui/backgrounds/level1.png",
                    false
                );

            case 2:
                return new LevelConfig(
                    2,
                    "The Broken Gate",
                    "Survive the orc ambush.",
                    new String[]{"GOBLIN", "GOBLIN", "ORC"},
                    "ui/backgrounds/level2.png",
                    false
                );

            case 3:
                return new LevelConfig(
                    3,
                    "Hall of Betrayal",
                    "Fight the first dark knight.",
                    new String[]{"GOBLIN", "ORC", "DARK_KNIGHT"},
                    "ui/backgrounds/level3.png",
                    false
                );

            case 4:
                return new LevelConfig(
                    4,
                    "Necromancer Crypt",
                    "Stop the dead from rising.",
                    new String[]{"ORC", "DARK_KNIGHT", "NECROMANCER"},
                    "ui/backgrounds/level4.png",
                    false
                );

            case 5:
                return new LevelConfig(
                    5,
                    "Siege of Black Castle",
                    "Break through the royal army.",
                    new String[]{"GOBLIN", "ORC", "ORC", "DARK_KNIGHT"},
                    "ui/backgrounds/level5.png",
                    false
                );

            case 6:
                return new LevelConfig(
                    6,
                    "The Dragon Valley",
                    "Face the first dragon.",
                    new String[]{"ORC", "DARK_KNIGHT", "DRAGON"},
                    "ui/backgrounds/level6.png",
                    true
                );

            case 7:
                return new LevelConfig(
                    7,
                    "War of the Five Houses",
                    "Survive the united enemy factions.",
                    new String[]{"GOBLIN", "ORC", "DARK_KNIGHT", "NECROMANCER", "DRAGON"},
                    "ui/backgrounds/level7.png",
                    true
                );

            case 8:
                return new LevelConfig(
                    8,
                    "Throne of Ashes",
                    "Defeat the cursed dragon guard.",
                    new String[]{"DARK_KNIGHT", "DARK_KNIGHT", "NECROMANCER", "DRAGON"},
                    "ui/backgrounds/level8.png",
                    true
                );

            case 9:
            default:
                return new LevelConfig(
                    9,
                    "The Final Realm",
                    "Kill the ancient dragon and claim the throne.",
                    new String[]{"NECROMANCER", "DARK_KNIGHT", "DRAGON", "DRAGON"},
                    "ui/backgrounds/level9.png",
                    true
                );
        }
    }
}
