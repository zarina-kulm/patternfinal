package com.thrones.patterns.screens;

import com.badlogic.gdx.Gdx;import com.badlogic.gdx.Input;import com.badlogic.gdx.Screen;import com.badlogic.gdx.audio.Music;import com.badlogic.gdx.audio.Sound;import com.badlogic.gdx.graphics.Color;import com.badlogic.gdx.graphics.GL20;import com.badlogic.gdx.graphics.Texture;import com.badlogic.gdx.graphics.g2d.BitmapFont;import com.badlogic.gdx.graphics.g2d.TextureRegion;import com.badlogic.gdx.graphics.glutils.ShapeRenderer;import com.badlogic.gdx.math.MathUtils;import com.thrones.patterns.WarOfRealms;import com.thrones.patterns.characters.Hero;import com.thrones.patterns.enemies.Enemy;import com.thrones.patterns.patterns.builder.BattleConfig;import com.thrones.patterns.patterns.builder.BattleConfigBuilder;import com.thrones.patterns.patterns.facade.BattleFacade;import com.thrones.patterns.patterns.factory.HeroFactory;import com.thrones.patterns.patterns.prototype.EnemyPrototypeRegistry;import com.thrones.patterns.patterns.singleton.GameStateSingleton;import com.thrones.patterns.utils.CharacterRenderer;

import java.util.ArrayList;import java.util.List;

public class GameScreen implements Screen {

    // ═══════════════════════════════════════
// KNIGHT ANIMATOR — ішкі класс
// ═══════════════════════════════════════
    private static class KnightAnimator {
        enum State { IDLE, RUN, ATTACK, HURT, DEAD, JUMP }

        private Texture idle;
        private final Texture[] run = new Texture[4];
        private final Texture[] attack = new Texture[3];
        private Texture hurt, dead, jump;

        private State currentState = State.IDLE;
        private float stateTimer = 0f;

        private static final float RUN_FRAME = 0.12f;
        private static final float ATK_FRAME = 0.1f;

        KnightAnimator() {
            idle = load("knight_idle.png");
            for (int i = 0; i < 4; i++) run[i] = load("knight_run" + (i + 1) + ".png");
            for (int i = 0; i < 3; i++) attack[i] = load("knight_attack" + (i + 1) + ".png");
            hurt = load("knight_hurt.png");
            dead = load("knight_dead.png");
            jump = load("knight_jump.png");
        }

        private Texture load(String name) {
            try {
                return new Texture(Gdx.files.internal(name));
            } catch (Exception e) {
                return null;
            }
        }

        void setState(State state) {
            if (currentState != state) {
                currentState = state;
                stateTimer = 0f;
            }
        }

        void update(float delta) {
            stateTimer += delta;
        }

        Texture getFrame() {
            switch (currentState) {
                case RUN:
                    int runFrame = (int) (stateTimer / RUN_FRAME) % 4;
                    return run[runFrame] != null ? run[runFrame] : idle;
                case ATTACK:
                    int attackFrame = Math.min((int) (stateTimer / ATK_FRAME), 2);
                    return attack[attackFrame] != null ? attack[attackFrame] : idle;
                case HURT:
                    return hurt != null ? hurt : idle;
                case DEAD:
                    return dead != null ? dead : idle;
                case JUMP:
                    return jump != null ? jump : idle;
                case IDLE:
                default:
                    return idle;
            }
        }

        boolean isAttackDone() {
            return currentState == State.ATTACK && stateTimer >= ATK_FRAME * 3;
        }

        void dispose() {
            if (idle != null) idle.dispose();
            for (Texture texture : run) if (texture != null) texture.dispose();
            for (Texture texture : attack) if (texture != null) texture.dispose();
            if (hurt != null) hurt.dispose();
            if (dead != null) dead.dispose();
            if (jump != null) jump.dispose();
        }
    }

    // ═══════════════════════════════════════
// MAIN FIELDS
// ═══════════════════════════════════════
    private final WarOfRealms game;
    private final BattleFacade facade;
    private final GameStateSingleton state;

    private Hero hero;
    private final List<Enemy> enemies = new ArrayList<>();

    private ShapeRenderer sr;
    private BitmapFont font;

    private Texture battleBackground;
    private Texture goblinTex;
    private Texture goblinSheet;
    private TextureRegion[] goblinFrames;
    private static final int GOBLIN_FRAME_COUNT = 8;
    private static final float GOBLIN_FRAME_TIME = 0.12f;
    private static final float GOBLIN_DRAW_WIDTH = 170f;
    private static final float GOBLIN_DRAW_HEIGHT = 125f;

    private Texture whitewalkerTex;
    private Texture archerTex;
    private Texture mageTex;
    private Texture bombTex;

    private Music battleMusic;
    private Sound swordSwingSound;
    private Sound swordHitSound;
    private Sound enemyDeathSound;
    private Sound fireSound;

    private KnightAnimator knightAnimator;

    private CampaignLevel currentLevel;
    private int targetIndex = 0;
    private float time = 0f;

    private float attackCD = 0f;
    private float abilityCD = 0f;
    private float shieldCD = 0f;

    private float swordRainCD = 0f;
    private float healCD = 0f;
    private float dashCD = 0f;

    private static final float SWORD_RAIN_CD = 10f;
    private static final float HEAL_CD = 12f;
    private static final float DASH_CD = 4f;

    private boolean dashActive = false;
    private float dashTimer = 0f;

    private static int bombsRemaining = 3;
    private float bombCD = 0f;
    private boolean bombEffect = false;
    private float bombEffectTimer = 0f;
    private float bombEffectX = 0f;
    private float bombEffectY = 0f;

    private static final int MAX_BOMBS = 3;
    private static final float BOMB_CD = 2.5f;
    private static final float BOMB_RADIUS = 360f;

    private boolean shieldActive = false;
    private float shieldTimer = 0f;

    private String message = "";
    private float msgTimer = 0f;
    private Color msgColor = Color.YELLOW;

    private boolean heroHit = false;
    private float heroHitTimer = 0f;
    private float heroInvincibleTimer = 0f;

    private boolean[] enemyHit;
    private float[] enemyHitTimer;
    private float[] enemyAttackTimer;
    private float[] enemyShakeTimer;
    private float[] enemyDeathTimer;
    private boolean[] enemyDying;
    private boolean[] enemyRewarded;

    private float levelStartTimer = 0f;
    private boolean screenChanging = false;

    private static final float HERO_SPEED = 300f;
    private static final float ENEMY_SPEED = 105f;

    private static final float ATTACK_CD = 0.4f;
    private static final float ABILITY_CD = 4f;
    private static final float SHIELD_CD = 6f;

    private static final float HERO_ATTACK_RANGE = 135f;
    private static final float ENEMY_ATTACK_RANGE = 60f;
    private static final float ENEMY_ATTACK_COOLDOWN = 1.25f;
    private static final float HERO_INVINCIBLE_TIME = 0.7f;
    private static final float KNOCKBACK = 55f;

    private static final float HERO_DAMAGE_MULTIPLIER = 0.55f;

    private static final float MIN_X = 20f;
    private static final float MAX_X = 1220f;
    private static final float MIN_Y = 70f;
    private static final float MAX_Y = 610f;

    private float heroVx = 0f;
    private float heroVy = 0f;
    private boolean isGrounded = true;

    private boolean isAttacking = false;
    private float attackAnimTimer = 0f;
    private float heroRunCycle = 0f;

    private List<Snowflake> snowflakesList;

    public GameScreen(WarOfRealms game) {
        this.game = game;
        this.facade = new BattleFacade();
        this.state = GameStateSingleton.getInstance();
    }

    @Override
    public void show() {
        sr = new ShapeRenderer();
        font = new BitmapFont();

        if (state.getLevel() < 1) {
            state.setLevel(1);
        }

        if (state.getLevel() == 1 && state.getScore() == 0) {
            bombsRemaining = MAX_BOMBS;
        }

        currentLevel = CampaignLevel.get(state.getLevel());

        loadAssets();
        setupLevel();
        initSnowfield(120);
    }

    private void loadAssets() {
        try {
            if (state.getLevel() <= 4) {
                battleBackground =
                    new Texture(Gdx.files.internal("background2.jpeg"));
            } else {
                battleBackground =
                    new Texture(Gdx.files.internal("backgroundpart2.jpeg"));
            }
        } catch (Exception e) {

            battleBackground = null;

            System.out.println("BACKGROUND ERROR!");
            e.printStackTrace();
        }

        try {
            battleMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/battle_theme.mp3"));
            battleMusic.setLooping(true);
            battleMusic.setVolume(0.25f);
            battleMusic.play();
        } catch (Exception e) {
            battleMusic = null;
        }

        try {
            swordSwingSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sword_swing.wav"));
        } catch (Exception e) {
            swordSwingSound = null;
        }

        try {
            swordHitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sword_hit.wav"));
        } catch (Exception e) {
            swordHitSound = null;
        }

        try {
            enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_die.wav"));
        } catch (Exception e) {
            enemyDeathSound = null;
        }

        try {
            fireSound = Gdx.audio.newSound(Gdx.files.internal("sounds/fire_spell.wav"));
        } catch (Exception e) {
            fireSound = null;
        }

        try {
            goblinTex = new Texture(Gdx.files.internal("goblin.png"));
            goblinTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        } catch (Exception e) {
            goblinTex = null;
        }

        try {
            goblinSheet = new Texture(Gdx.files.internal("goblin_spritesheet.png"));
            goblinSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            int frameWidth = goblinSheet.getWidth() / GOBLIN_FRAME_COUNT;
            int frameHeight = goblinSheet.getHeight();

            TextureRegion[][] split = TextureRegion.split(goblinSheet, frameWidth, frameHeight);
            goblinFrames = new TextureRegion[GOBLIN_FRAME_COUNT];

            for (int i = 0; i < GOBLIN_FRAME_COUNT; i++) {
                goblinFrames[i] = split[0][i];
            }
        } catch (Exception e) {
            goblinSheet = null;
            goblinFrames = null;
        }

        try {
            whitewalkerTex = new Texture(Gdx.files.internal("whitewalker.png"));
        } catch (Exception e) {
            whitewalkerTex = null;
        }

        try {
            archerTex = new Texture(Gdx.files.internal("archer.png"));
        } catch (Exception e) {
            archerTex = null;
        }

        try {
            mageTex = new Texture(Gdx.files.internal("mage.png"));
        } catch (Exception e) {
            mageTex = null;
        }

        try {
            bombTex = new Texture(Gdx.files.internal("bomb2.png"));
        } catch (Exception e) {
            bombTex = null;
        }

        if ("KNIGHT".equals(state.getSelectedHeroType())) {
            knightAnimator = new KnightAnimator();
        }
    }

    private void setupLevel() {
        String heroType = state.getSelectedHeroType();

        if (heroType == null ||
            (!heroType.equals("KNIGHT") &&
                !heroType.equals("MAGE") &&
                !heroType.equals("ARCHER"))) {
            heroType = "KNIGHT";
            state.setSelectedHeroType("KNIGHT");
        }

        hero = HeroFactory.createHero(heroType, "Selected House");
        hero.setPosition(120, 180);

        enemies.clear();

        EnemyPrototypeRegistry registry = EnemyPrototypeRegistry.getInstance();

        for (String enemyType : currentLevel.enemies) {
            try {
                Enemy enemy = registry.spawn(enemyType);
                scaleEnemyForLevel(enemy);
                enemies.add(enemy);
            } catch (Exception e) {
                Enemy enemy = registry.spawn("GOBLIN");
                scaleEnemyForLevel(enemy);
                enemies.add(enemy);
            }
        }

        float[][] spawnPoints = {
            {760, 180}, {930, 260}, {840, 390},
            {1040, 170}, {1120, 470}, {1210, 320}
        };

        for (int i = 0; i < enemies.size(); i++) {
            float[] point = spawnPoints[Math.min(i, spawnPoints.length - 1)];
            enemies.get(i).setPosition(point[0], point[1]);
            enemies.get(i).setTarget(hero);
        }

        int size = enemies.size();
        enemyHit = new boolean[size];
        enemyHitTimer = new float[size];
        enemyAttackTimer = new float[size];
        enemyShakeTimer = new float[size];
        enemyDeathTimer = new float[size];
        enemyDying = new boolean[size];
        enemyRewarded = new boolean[size];

        BattleConfig config = new BattleConfigBuilder()
            .setBattleName("Level " + state.getLevel() + ": " + currentLevel.title)
            .setHero(hero)
            .setWaveCount(state.getLevel())
            .build();

        facade.setupBattle(config);

        msg("LEVEL " + state.getLevel() + " — " + currentLevel.title, Color.GOLD);
    }


    private void scaleEnemyForLevel(Enemy enemy) {
        float hpMultiplier = 1f;

        if (state.getLevel() >= 4) {
            hpMultiplier = 1.45f;
        }

        if (state.getLevel() >= 6) {
            hpMultiplier = 1.75f;
        }

        if (hpMultiplier <= 1f) {
            return;
        }

        float bonusHp = enemy.getMaxHp() * (hpMultiplier - 1f);

        // Most Enemy classes do not expose setHp/setMaxHp.
        // Negative damage safely works as a small HP boost in this project.
        enemy.takeDamage(-bonusHp);
    }

    @Override
    public void render(float delta) {
        time += delta;

        Gdx.gl.glClearColor(0.01f, 0.005f, 0.012f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        drawBattleBackground();
        updateSnowfield(delta);
        renderSnowfield(sr);
        drawCharacters();

        game.batch.begin();
        drawHUD();
        drawControls();
        drawMessage();
        game.batch.end();

        drawHPBars();
        drawTarget();

        checkLevelEnd(delta);
    }

    private void update(float delta) {
        updateHeroMovement(delta);
        updateEnemies(delta);
        updateTimers(delta);
        handleInput();
    }

    private void updateHeroMovement(float delta) {
        float nextX = hero.getX();
        float nextY = hero.getY();

        heroVx = 0f;
        heroVy = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            nextX -= HERO_SPEED * delta;
            heroVx = -HERO_SPEED;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            nextX += HERO_SPEED * delta;
            heroVx = HERO_SPEED;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            nextY += HERO_SPEED * delta;
            heroVy = HERO_SPEED;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            nextY -= HERO_SPEED * delta;
            heroVy = -HERO_SPEED;
        }

        nextX = MathUtils.clamp(nextX, MIN_X, MAX_X);
        nextY = MathUtils.clamp(nextY, MIN_Y, MAX_Y);

        isGrounded = true;
        hero.setPosition(nextX, nextY);
        hero.update(delta);
    }

    private void updateEnemies(float delta) {
        float speedBonus = 1f + state.getLevel() * 0.075f;

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (!enemy.isAlive()) continue;

            float dx = hero.getX() - enemy.getX();
            float dy = hero.getY() - enemy.getY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > ENEMY_ATTACK_RANGE) {
                if (distance < 1f) distance = 1f;

                float moveX = dx / distance;
                float moveY = dy / distance;

                enemy.setPosition(
                    MathUtils.clamp(enemy.getX() + moveX * ENEMY_SPEED * speedBonus * delta, MIN_X, MAX_X),
                    MathUtils.clamp(enemy.getY() + moveY * ENEMY_SPEED * speedBonus * delta, MIN_Y, MAX_Y)
                );
            } else {
                if (!shieldActive && heroInvincibleTimer <= 0f && enemyAttackTimer[i] <= 0f) {
                    float enemyDamageMultiplier = 0.95f;

                    if (state.getLevel() >= 4) {
                        enemyDamageMultiplier = 1.25f;
                    }

                    if (state.getLevel() >= 6) {
                        enemyDamageMultiplier = 1.55f;
                    }

                    float enemyDamage = enemy.getAttack() * 0.65f;

                    hero.takeDamage(enemyDamage);

                    heroHit = true;
                    heroHitTimer = 0.25f;
                    heroInvincibleTimer = HERO_INVINCIBLE_TIME;
                    enemyAttackTimer[i] = ENEMY_ATTACK_COOLDOWN;

                    float knockX = dx < 0 ? 28f : -28f;
                    float knockY = dy < 0 ? 14f : -14f;

                    hero.setPosition(
                        MathUtils.clamp(hero.getX() + knockX, MIN_X, MAX_X),
                        MathUtils.clamp(hero.getY() + knockY, MIN_Y, MAX_Y)
                    );

                    msg(enemy.getName() + " attacked you! -" + (int) enemyDamage + " HP", Color.RED);
                }
            }

            enemy.update(delta);
        }
    }

    private void updateTimers(float delta) {
        if (attackCD > 0) attackCD -= delta;
        if (abilityCD > 0) abilityCD -= delta;
        if (shieldCD > 0) shieldCD -= delta;
        if (msgTimer > 0) msgTimer -= delta;
        if (swordRainCD > 0) swordRainCD -= delta;
        if (healCD > 0) healCD -= delta;
        if (dashCD > 0) dashCD -= delta;
        if (bombCD > 0) bombCD -= delta;

        if (dashActive) {
            dashTimer -= delta;

            if (dashTimer <= 0) {
                dashActive = false;
            }
        }

        if (bombEffect) {
            bombEffectTimer -= delta;
            if (bombEffectTimer <= 0f) {
                bombEffect = false;
            }
        }

        if (shieldActive) {
            shieldTimer -= delta;
            if (shieldTimer <= 0) shieldActive = false;
        }

        if (heroHitTimer > 0) {
            heroHitTimer -= delta;
            heroHit = heroHitTimer > 0;
        }

        if (heroInvincibleTimer > 0) {
            heroInvincibleTimer -= delta;
        }

        if (attackAnimTimer > 0) {
            attackAnimTimer -= delta;
            if (attackAnimTimer <= 0) {
                isAttacking = false;
            }
        }

        if (enemyHitTimer != null) {
            for (int i = 0; i < enemyHitTimer.length; i++) {
                if (enemyHitTimer[i] > 0) {
                    enemyHitTimer[i] -= delta;
                    enemyHit[i] = enemyHitTimer[i] > 0;
                }

                if (enemyAttackTimer[i] > 0) {
                    enemyAttackTimer[i] -= delta;
                }

                if (enemyShakeTimer[i] > 0) {
                    enemyShakeTimer[i] -= delta;
                }

                if (enemyDeathTimer[i] > 0) {
                    enemyDeathTimer[i] -= delta;
                }
            }
        }

        if (heroVx != 0 || heroVy != 0) {
            heroRunCycle += delta * 8f;
        } else {
            heroRunCycle = 0f;
        }

        if (knightAnimator != null) {
            knightAnimator.update(delta);

            if (!hero.isAlive()) {
                knightAnimator.setState(KnightAnimator.State.DEAD);
            } else if (heroHit) {
                knightAnimator.setState(KnightAnimator.State.HURT);
            } else if (isAttacking) {
                knightAnimator.setState(KnightAnimator.State.ATTACK);
            } else if (heroVx != 0 || heroVy != 0) {
                knightAnimator.setState(KnightAnimator.State.RUN);
            } else {
                knightAnimator.setState(KnightAnimator.State.IDLE);
            }

            if (knightAnimator.isAttackDone()) {
                isAttacking = false;
            }
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()) {
            normalAttack();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            activateShield();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            useSpecialAbility();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            summonSwordRain();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            useBomb();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            useHealPotion();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)) {
            dash();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) selectTarget(0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) selectTarget(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) selectTarget(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) selectTarget(3);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) selectTarget(4);

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            nextTarget();
            Enemy target = getTarget();
            if (target != null) msg("Target: " + target.getName(), Color.YELLOW);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            stopMusic();
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void normalAttack() {
        if (attackCD > 0) return;

        Enemy target = getTarget();
        if (target == null) return;

        float dx = target.getX() - hero.getX();
        float dy = target.getY() - hero.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > HERO_ATTACK_RANGE) {
            msg("Too far! Run closer!", Color.LIGHT_GRAY);
            return;
        }

        int index = enemies.indexOf(target);

        if (swordSwingSound != null) swordSwingSound.play(0.6f);

        float damage = hero.getAttack() * HERO_DAMAGE_MULTIPLIER * getWeaponBonus();
        target.takeDamage(damage);

        if (distance < 1f) distance = 1f;

        target.setPosition(
            MathUtils.clamp(target.getX() + dx / distance * KNOCKBACK, MIN_X, MAX_X),
            MathUtils.clamp(target.getY() + dy / distance * KNOCKBACK, MIN_Y, MAX_Y)
        );

        if (swordHitSound != null) swordHitSound.play(0.7f);

        rewardIfDead(target);

        attackCD = ATTACK_CD;
        isAttacking = true;
        attackAnimTimer = 0.3f;

        if (index >= 0 && index < enemyHit.length) {
            enemyHit[index] = true;
            enemyHitTimer[index] = 0.15f;
        }

        if (index >= 0 && index < enemyShakeTimer.length) {
            enemyShakeTimer[index] = 0.2f;
        }

        msg(getWeaponName() + " strike: -" + (int) damage + " DMG", Color.WHITE);
    }

    private void activateShield() {
        if (shieldCD > 0) {
            msg("Shield cooldown: " + (int) shieldCD + "s", Color.GRAY);
            return;
        }

        shieldActive = true;
        shieldTimer = 2f;
        shieldCD = SHIELD_CD;

        msg("[Q] Defensive shield active!", Color.CYAN);
    }

    private void useSpecialAbility() {
        if (abilityCD > 0) {
            msg("Ability cooldown: " + (int) abilityCD + "s", Color.GRAY);
            return;
        }

        Enemy target = getTarget();
        if (target == null) return;

        float weaponBonus = getWeaponBonus();

        if (hero.getType().equals("KNIGHT")) {
            float damage = hero.getAttack() * weaponBonus * 1.8f;
            target.takeDamage(damage);
            shieldActive = true;
            shieldTimer = 1.2f;
            msg("[E] Dragon Blood Slash! -" + (int) damage + " DMG", Color.ORANGE);
        } else if (hero.getType().equals("MAGE")) {
            float damage = hero.getAttack() * weaponBonus * 2.4f;
            target.takeDamage(damage);
            msg("[E] Golden Fire Nova! -" + (int) damage + " DMG", Color.GOLD);
        } else {
            float damage = hero.getAttack() * weaponBonus * 1.6f;

            for (Enemy enemy : enemies) {
                if (enemy.isAlive()) {
                    float dx = enemy.getX() - target.getX();
                    float dy = enemy.getY() - target.getY();
                    float distance = (float) Math.sqrt(dx * dx + dy * dy);

                    if (distance < 180f) {
                        enemy.takeDamage(damage);
                        rewardIfDead(enemy);
                    }
                }
            }

            msg("[E] Hunter's Rain! Area damage.", Color.GREEN);
        }

        if (fireSound != null) fireSound.play(0.75f);

        int index = enemies.indexOf(target);
        if (index >= 0 && index < enemyHit.length) {
            enemyHit[index] = true;
            enemyHitTimer[index] = 0.2f;
        }

        if (index >= 0 && index < enemyShakeTimer.length) {
            enemyShakeTimer[index] = 0.25f;
        }

        rewardIfDead(target);
        abilityCD = ABILITY_CD;
    }


    private void useBomb() {
        if (bombsRemaining <= 0) {
            msg("No bombs left!", Color.RED);
            return;
        }

        if (bombCD > 0) {
            msg("Bomb cooldown: " + (int) bombCD + "s", Color.GRAY);
            return;
        }

        bombsRemaining--;
        bombCD = BOMB_CD;

        bombEffect = true;
        bombEffectTimer = 0.55f;
        bombEffectX = hero.getX() + 45f;
        bombEffectY = hero.getY() + 75f;

        boolean hitAny = false;

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (!enemy.isAlive()) continue;

            // Bomb is a rare ultimate item: it kills every alive enemy on the current level.
            enemy.takeDamage(enemy.getMaxHp() * 10f);
            hitAny = true;

            if (i < enemyHit.length) {
                enemyHit[i] = true;
                enemyHitTimer[i] = 0.35f;
            }

            if (i < enemyShakeTimer.length) {
                enemyShakeTimer[i] = 0.45f;
            }

            rewardIfDead(enemy);
        }

        if (fireSound != null) {
            fireSound.play(1f);
        }

        msg(hitAny ? "BOOM! Enemies destroyed!" : "BOOM! No enemies left!", Color.ORANGE);
    }

    private void rewardIfDead(Enemy enemy) {
        int index = enemies.indexOf(enemy);

        if (index < 0) return;

        if (!enemy.isAlive() && !enemyRewarded[index]) {
            enemyRewarded[index] = true;

            if (enemyDeathSound != null) enemyDeathSound.play(0.8f);

            state.addGold(enemy.getGoldReward());
            state.addScore(enemy.getExpReward() * 10);

            enemyDying[index] = true;
            enemyDeathTimer[index] = 0.5f;
        }
    }

    private float getWeaponBonus() {
        return 1f + state.getLevel() * 0.12f;
    }

    private String getWeaponName() {
        int level = state.getLevel();

        if (level <= 2) return "Iron Sword";
        if (level <= 4) return "Steel Sword";
        if (level <= 6) return "Royal Blade";
        if (level <= 8) return "Dragon Blade";

        return "Thronebreaker";
    }

    private void drawBattleBackground() {
        game.batch.begin();

        if (battleBackground != null) {
            game.batch.setColor(1f, 1f, 1f, 1f);
            game.batch.draw(battleBackground, 0, 0, 1280, 720);
            game.batch.setColor(Color.WHITE);
        }

        game.batch.end();
    }


    private TextureRegion getGoblinAnimationFrame(int enemyIndex, Enemy enemy) {
        if (goblinFrames == null || goblinFrames.length == 0) return null;

        float dx = hero.getX() - enemy.getX();
        float dy = hero.getY() - enemy.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        int frameIndex;

        if (distance <= ENEMY_ATTACK_RANGE + 10f) {
            // Attack frames: last 3 frames from the spritesheet.
            frameIndex = 5 + ((int) (time / GOBLIN_FRAME_TIME) % 3);
        } else {
            // Run frames: middle frames from the spritesheet.
            frameIndex = 1 + ((int) (time / GOBLIN_FRAME_TIME + enemyIndex) % 4);
        }

        frameIndex = MathUtils.clamp(frameIndex, 0, GOBLIN_FRAME_COUNT - 1);
        return goblinFrames[frameIndex];
    }

    private void drawCharacters() {
        game.batch.begin();

        if (hero.getType().equals("KNIGHT") && knightAnimator != null) {
            Texture frame = knightAnimator.getFrame();

            if (frame != null) {
                if (heroHit) {
                    float flicker = (float) (Math.sin(time * 30f) * 0.5f + 0.5f);
                    game.batch.setColor(1f, flicker * 0.4f, flicker * 0.4f, 1f);
                } else {
                    game.batch.setColor(Color.WHITE);
                }

                game.batch.draw(frame, hero.getX() - 20, hero.getY(), 130, 170);
                game.batch.setColor(Color.WHITE);
            } else {
                game.batch.end();
                CharacterRenderer.drawKnight(sr, hero.getX(), hero.getY(), 1f, time, heroHit);
                game.batch.begin();
            }
        } else {
            Texture texture = hero.getType().equals("MAGE") ? mageTex : archerTex;

            if (texture != null) {
                float bobY = (heroVx != 0 || heroVy != 0) ? (float) (Math.sin(heroRunCycle) * 5f) : 0f;
                float attackOffX = 0f;

                if (isAttacking) {
                    float progress = 1f - (attackAnimTimer / 0.3f);
                    attackOffX = (float) (Math.sin(progress * Math.PI)) * 25f;
                }

                if (heroHit) game.batch.setColor(1f, 0.3f, 0.3f, 1f);
                else game.batch.setColor(Color.WHITE);

                game.batch.draw(texture, hero.getX() - 10 + attackOffX, hero.getY() + bobY, 90, 150);
                game.batch.setColor(Color.WHITE);
            } else {
                game.batch.end();

                if (hero.getType().equals("MAGE")) {
                    CharacterRenderer.drawMage(sr, hero.getX(), hero.getY(), 1f, time, heroHit);
                } else if (hero.getType().equals("ARCHER")) {
                    CharacterRenderer.drawArcher(sr, hero.getX(), hero.getY(), 1f, time, heroHit);
                } else {
                    CharacterRenderer.drawKnight(sr, hero.getX(), hero.getY(), 1f, time, heroHit);
                }

                game.batch.begin();
            }
        }

        if (isAttacking) {
            game.batch.end();

            float progress = 1f - (attackAnimTimer / 0.3f);
            float alpha = (float) Math.sin(progress * Math.PI);
            float direction = 1f;

            Enemy target = getTarget();
            if (target != null && target.getX() < hero.getX()) {
                direction = -1f;
            }

            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(1f, 0.9f, 0.3f, alpha * 0.8f);
            float sx = hero.getX() + 45f;
            float sy = hero.getY() + 80f;
            sr.rectLine(sx, sy, sx + direction * 60f * alpha, sy - 25f * alpha, 5f);
            sr.rectLine(sx, sy, sx + direction * 45f * alpha, sy + 15f * alpha, 3f);

            sr.end();
            game.batch.begin();
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);

            if (!enemy.isAlive()) {
                if (i < enemyDying.length && enemyDying[i] && enemyDeathTimer[i] > 0) {
                    float progress = 1f - (enemyDeathTimer[i] / 0.5f);
                    float alpha = enemyDeathTimer[i] / 0.5f;
                    Texture deadTexture = enemy.getType().equals("GOBLIN") ? goblinTex : whitewalkerTex;

                    if (deadTexture != null) {
                        game.batch.setColor(1f, 0.3f, 0.3f, alpha);
                        game.batch.draw(
                            deadTexture,
                            enemy.getX() - 5,
                            enemy.getY() - progress * 40f,
                            70,
                            110 * (1f - progress * 0.5f)
                        );
                        game.batch.setColor(Color.WHITE);
                    }
                }

                continue;
            }

            boolean hit = i < enemyHit.length && enemyHit[i];

            float shakeX = 0f;
            if (i < enemyShakeTimer.length && enemyShakeTimer[i] > 0) {
                shakeX = (float) (Math.sin(enemyShakeTimer[i] * 40f) * 6f);
            }

            float enemyBob = (float) (Math.sin(time * 4f + i) * 3f);

            Texture enemyTexture;
            float width;
            float height;

            switch (enemy.getType()) {
                case "GOBLIN":
                    enemyTexture = goblinTex;
                    width = 60f;
                    height = 95f;
                    break;
                case "DRAGON":
                    enemyTexture = whitewalkerTex;
                    width = 110f;
                    height = 130f;
                    break;
                default:
                    enemyTexture = whitewalkerTex;
                    width = 75f;
                    height = 120f;
                    break;
            }

            if (enemy.getType().equals("GOBLIN") && goblinFrames != null) {
                TextureRegion goblinFrame = getGoblinAnimationFrame(i, enemy);

                if (hit) game.batch.setColor(1f, 0.3f, 0.3f, 1f);
                else game.batch.setColor(Color.WHITE);

                game.batch.draw(
                    goblinFrame,
                    enemy.getX() - 45 + shakeX,
                    enemy.getY() - 10 + enemyBob,
                    GOBLIN_DRAW_WIDTH,
                    GOBLIN_DRAW_HEIGHT
                );

                game.batch.setColor(Color.WHITE);
            } else if (enemyTexture != null) {
                if (hit) game.batch.setColor(1f, 0.3f, 0.3f, 1f);
                else game.batch.setColor(Color.WHITE);

                game.batch.draw(
                    enemyTexture,
                    enemy.getX() - 5 + shakeX,
                    enemy.getY() + enemyBob,
                    width,
                    height
                );

                game.batch.setColor(Color.WHITE);
            } else {
                game.batch.end();

                switch (enemy.getType()) {
                    case "GOBLIN":
                        CharacterRenderer.drawGoblin(sr, enemy.getX() + shakeX, enemy.getY(), 0.9f, time, hit);
                        break;
                    case "ORC":
                        CharacterRenderer.drawOrc(sr, enemy.getX() + shakeX, enemy.getY(), 0.85f, time, hit);
                        break;
                    case "DARK_KNIGHT":
                        CharacterRenderer.drawDarkKnight(sr, enemy.getX() + shakeX, enemy.getY(), 0.95f, time, hit);
                        break;
                    case "NECROMANCER":
                        CharacterRenderer.drawNecromancer(sr, enemy.getX() + shakeX, enemy.getY(), 0.9f, time, hit);
                        break;
                    case "DRAGON":
                        CharacterRenderer.drawDragon(sr, enemy.getX() - 20 + shakeX, enemy.getY(), 0.8f, time, hit);
                        break;
                }

                game.batch.begin();
            }
        }

        game.batch.end();

        if (shieldActive) {
            sr.begin(ShapeRenderer.ShapeType.Line);
            float pulse = (float) (Math.sin(time * 10f) * 0.5f + 0.5f);
            sr.setColor(0f, pulse, pulse, 1f);
            sr.circle(hero.getX() + 40, hero.getY() + 60, 55, 20);
            sr.end();
        }

        if (bombEffect) {
            float progress = Math.max(0f, bombEffectTimer / 0.55f);
            float expand = 1f - progress;

            game.batch.begin();

            if (bombTex != null) {
                game.batch.setColor(Color.WHITE);

                float size = 120f + 90f * expand;

                game.batch.draw(
                    bombTex,
                    bombEffectX - size / 2f,
                    bombEffectY - size / 2f,
                    size,
                    size
                );
            }

            font.getData().setScale(2.8f);
            font.setColor(Color.ORANGE);
            font.draw(game.batch, "BOOM!", bombEffectX - 85f, bombEffectY + 130f);

            font.getData().setScale(1f);
            game.batch.setColor(Color.WHITE);

            game.batch.end();
        }
    }

    private void drawHUD() {
        font.getData().setScale(1f);

        font.setColor(Color.GOLD);
        font.draw(game.batch, "THRONES OF PATTERNS", 500, 714);

        font.getData().setScale(0.9f);

        font.setColor(Color.WHITE);
        font.draw(game.batch, "Level " + state.getLevel() + "/7", 20, 695);

        font.setColor(Color.GREEN);
        font.draw(game.batch, "HP: " + (int) hero.getHp() + "/" + (int) hero.getMaxHp(), 20, 670);

        font.setColor(Color.YELLOW);
        font.draw(game.batch, "Gold: " + state.getGold(), 20, 645);

        font.setColor(Color.ORANGE);
        font.draw(game.batch, "Bombs: " + bombsRemaining + "/" + MAX_BOMBS, 20, 620);

        if (shieldActive) {
            font.setColor(Color.CYAN);
            font.draw(game.batch, "Shield active", 20, 595);
        }

        font.setColor(Color.LIGHT_GRAY);
        font.draw(game.batch, "Enemies:", 1030, 695);

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);

            font.setColor(
                i == targetIndex && enemy.isAlive()
                    ? Color.YELLOW
                    : enemy.isAlive()
                    ? Color.WHITE
                    : Color.DARK_GRAY
            );

            float shownHp = Math.max(0f, enemy.getHp());

            font.draw(
                game.batch,
                (i + 1) + ". " + enemy.getName() +
                    (enemy.isAlive() ? " HP: " + (int) shownHp : " [DEAD]"),
                1030,
                670 - i * 22
            );
        }

        font.getData().setScale(1f);
    }

    private void drawControls() {
        font.getData().setScale(0.82f);

        int y = 155;
        int spacing = 20;

        font.setColor(Color.WHITE);
        font.draw(game.batch, "[W/A/S/D] Move", 20, y);

        font.setColor(attackCD > 0 ? Color.GRAY : Color.GREEN);
        font.draw(game.batch, "[SPACE] Attack" + (attackCD > 0 ? " (Cooling)" : " [READY]"), 20, y - spacing);

        cd("[Q]", "Shield", shieldCD, Color.CYAN, y - spacing * 2);
        cd("[E]", "Hero Skill", abilityCD, Color.ORANGE, y - spacing * 3);
        cd("[B]", "Bomb", bombCD, Color.ORANGE, y - spacing * 4);

        font.setColor(Color.DARK_GRAY);
        font.draw(game.batch, "[TAB] Target  |  [ESC] Menu", 20, y - spacing * 5 - 4);

        font.getData().setScale(1f);
    }

    private void cd(String key, String name, float cooldown, Color color, float y) {
        if (cooldown > 0) {
            font.setColor(Color.GRAY);
            font.draw(game.batch, key + " " + name + " (" + (int) cooldown + "s)", 20, y);
        } else {
            font.setColor(color);
            font.draw(game.batch, key + " " + name + " [READY]", 20, y);
        }
    }

    private void drawMessage() {
        if (msgTimer <= 0) return;

        font.getData().setScale(1.4f);
        font.setColor(msgColor.r, msgColor.g, msgColor.b, Math.min(1f, msgTimer));
        font.draw(game.batch, message, 360, 420);
        font.getData().setScale(1f);
    }

    private void drawHPBars() {
        sr.begin(ShapeRenderer.ShapeType.Filled);

        float hp = MathUtils.clamp(hero.getHp() / hero.getMaxHp(), 0f, 1f);

        sr.setColor(0.15f, 0.15f, 0.15f, 1f);
        sr.rect(hero.getX(), hero.getY() + 175, 130, 10);

        sr.setColor(hp > 0.5f ? Color.GREEN : hp > 0.25f ? Color.ORANGE : Color.RED);
        sr.rect(hero.getX(), hero.getY() + 175, 130 * hp, 10);

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;

            float ep = MathUtils.clamp(enemy.getHp() / enemy.getMaxHp(), 0f, 1f);

            sr.setColor(0.15f, 0.15f, 0.15f, 1f);
            sr.rect(enemy.getX(), enemy.getY() + 125, 75, 8);

            sr.setColor(Color.RED);
            sr.rect(enemy.getX(), enemy.getY() + 125, 75 * ep, 8);
        }

        sr.end();
    }

    private void drawTarget() {
        if (targetIndex < enemies.size() && enemies.get(targetIndex).isAlive()) {
            Enemy target = enemies.get(targetIndex);

            float pulse = (float) (Math.sin(time * 4f) * 0.4f + 0.6f);

            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(pulse, pulse * 0.8f, 0f, 1f);

            sr.rect(target.getX() - 4, target.getY() - 4, 80, 130);
            sr.triangle(
                target.getX() + 32,
                target.getY() + 135,
                target.getX() + 40,
                target.getY() + 150,
                target.getX() + 48,
                target.getY() + 135
            );

            sr.end();
        }
    }

    private void checkLevelEnd(float delta) {
        if (screenChanging) return;

        levelStartTimer += delta;
        if (levelStartTimer <= 1.5f) return;

        // Hero өлді ме?
        if (!hero.isAlive()) {
            screenChanging = true;
            stopMusic();
            state.setGameOver(true);
            game.setScreen(new GameOverScreen(game));
            return;
        }
        if (bombEffect) {
            return;
        }
        // Барлық жау өлді ме?
        boolean allDead = true;
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                allDead = false;
                break;
            }
        }

        if (allDead) {
            screenChanging = true;
            stopMusic();

            if (state.getLevel() >= 7) {
                state.setVictory(true);
                game.setScreen(new VictoryScreen(game));
            } else {
                state.nextLevel();
                game.setScreen(new GameScreen(game));
            }
        }
    }

    private void selectTarget(int index) {
        if (index < enemies.size() && enemies.get(index).isAlive()) {
            targetIndex = index;
            msg("Target: " + enemies.get(index).getName(), Color.YELLOW);
        }
    }

    private void nextTarget() {
        for (int i = 1; i <= enemies.size(); i++) {
            int next = (targetIndex + i) % enemies.size();

            if (enemies.get(next).isAlive()) {
                targetIndex = next;
                return;
            }
        }
    }

    private Enemy getTarget() {
        if (targetIndex < enemies.size() && enemies.get(targetIndex).isAlive()) {
            return enemies.get(targetIndex);
        }

        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).isAlive()) {
                targetIndex = i;
                return enemies.get(i);
            }
        }

        return null;
    }

    private void msg(String text, Color color) {
        message = text;
        msgTimer = 2.5f;
        msgColor = color;
    }

    private void stopMusic() {
        if (battleMusic != null) {
            battleMusic.stop();
        }
    }

    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        stopMusic();
    }

    @Override
    public void dispose() {
        if (sr != null) sr.dispose();
        if (font != null) font.dispose();
        if (battleBackground != null) battleBackground.dispose();
        if (battleMusic != null) battleMusic.dispose();
        if (swordSwingSound != null) swordSwingSound.dispose();
        if (swordHitSound != null) swordHitSound.dispose();
        if (enemyDeathSound != null) enemyDeathSound.dispose();
        if (fireSound != null) fireSound.dispose();
        if (goblinTex != null) goblinTex.dispose();
        if (goblinSheet != null) goblinSheet.dispose();
        if (whitewalkerTex != null) whitewalkerTex.dispose();
        if (archerTex != null) archerTex.dispose();
        if (mageTex != null) mageTex.dispose();
        if (bombTex != null) bombTex.dispose();
        if (knightAnimator != null) knightAnimator.dispose();
    }

    // ═══════════════════════════════════════
// CAMPAIGN LEVELS
// ═══════════════════════════════════════
    private static class CampaignLevel {
        int level;
        String title;
        String objective;
        String background;
        String[] enemies;

        CampaignLevel(int level, String title, String objective, String background, String[] enemies) {
            this.level = level;
            this.title = title;
            this.objective = objective;
            this.background = background;
            this.enemies = enemies;
        }

        static CampaignLevel get(int level) {
            switch (level) {
                case 1:
                    return new CampaignLevel(1, "Border Ambush", "Defeat the first scouts.", "background2.jpeg",
                        new String[]{"GOBLIN", "GOBLIN"});
                case 2:
                    return new CampaignLevel(2, "Burned Village", "Survive the orc ambush.", "background2.jpeg",
                        new String[]{"GOBLIN", "GOBLIN", "ORC"});
                case 3:
                    return new CampaignLevel(3, "Castle Gate", "Break through the enemy formation.", "background2.jpeg",
                        new String[]{"GOBLIN", "ORC", "ORC", "DARK_KNIGHT"});
                case 4:
                    return new CampaignLevel(4, "Northern War", "Defeat the commander of the first realm.", "background2.jpeg",
                        new String[]{"ORC", "DARK_KNIGHT", "DARK_KNIGHT", "NECROMANCER"});
                case 5:
                    return new CampaignLevel(5, "Frozen Road", "Enter the second realm.", "backgroundpart2.jpeg",
                        new String[]{"ORC", "DARK_KNIGHT", "NECROMANCER"});
                case 6:
                    return new CampaignLevel(6, "Dragon Valley", "Survive the dragon assault.", "backgroundpart2.jpeg",
                        new String[]{"DARK_KNIGHT", "NECROMANCER", "DRAGON"});
                case 7:
                default:
                    return new CampaignLevel(7, "The Black Throne", "Defeat the final army and claim the throne.", "backgroundpart2.jpeg",
                        new String[]{"DARK_KNIGHT", "NECROMANCER", "DRAGON", "DRAGON"});
            }
        }
    }

    // ═══════════════════════════════════════
// SNOWFLAKES
// ═══════════════════════════════════════
    private static class Snowflake {
        float x;
        float y;
        float speed;
        float size;
        float alpha;
        boolean fading;
    }

    private void initSnowfield(int count) {
        snowflakesList = new ArrayList<>();
        for (int i = 0; i < count; i++) snowflakesList.add(createSnowflake(true));
    }

    private Snowflake createSnowflake(boolean randomY) {
        Snowflake snowflake = new Snowflake();
        snowflake.x = MathUtils.random(0, 1280);
        snowflake.y = randomY ? MathUtils.random(0, 720) : 720;
        snowflake.speed = MathUtils.random(150f, 350f);
        snowflake.size = MathUtils.random(1.5f, 3.5f);
        snowflake.alpha = MathUtils.random(0.3f, 0.9f);
        snowflake.fading = MathUtils.randomBoolean();
        return snowflake;
    }

    private void updateSnowfield(float delta) {
        if (snowflakesList == null) return;

        float multiplier = 1f + state.getLevel() * 0.15f;

        for (int i = 0; i < snowflakesList.size(); i++) {
            Snowflake snowflake = snowflakesList.get(i);

            snowflake.y -= snowflake.speed * delta * multiplier;
            snowflake.x -= snowflake.speed * 0.3f * delta;

            if (snowflake.fading) {
                snowflake.alpha -= delta * 0.5f;
                if (snowflake.alpha <= 0.2f) snowflake.fading = false;
            } else {
                snowflake.alpha += delta * 0.5f;
                if (snowflake.alpha >= 0.9f) snowflake.fading = true;
            }

            if (snowflake.y < 0 || snowflake.x < 0) {
                snowflakesList.set(i, createSnowflake(false));
            }
        }
    }

    private void renderSnowfield(ShapeRenderer renderer) {
        if (snowflakesList == null) return;

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Snowflake snowflake : snowflakesList) {
            renderer.setColor(0.85f, 0.95f, 1f, snowflake.alpha);
            renderer.circle(snowflake.x, snowflake.y, snowflake.size);
        }

        renderer.end();
    }
    private void summonSwordRain() {

        if (swordRainCD > 0) {
            msg("Sword Rain cooldown!", Color.GRAY);
            return;
        }

        for (Enemy enemy : enemies) {

            if (!enemy.isAlive()) continue;

            float damage = hero.getAttack() * 2.5f;

            enemy.takeDamage(damage);

            rewardIfDead(enemy);
        }

        swordRainCD = SWORD_RAIN_CD;

        msg("⚔ Sword Rain activated!", Color.GOLD);
    }

    private void useHealPotion() {
        if (healCD > 0) {
            msg("Potion cooldown!", Color.GRAY);
            return;
        }

        float healAmount = 45f;

        float missingHp = hero.getMaxHp() - hero.getHp();
        float realHeal = Math.min(healAmount, missingHp);

        if (realHeal <= 0) {
            msg("HP already full!", Color.GRAY);
            return;
        }

        hero.heal(realHeal);

        healCD = HEAL_CD;

        msg("+" + (int) realHeal + " HP restored!", Color.GREEN);
    }
    private void dash() {

        if (dashCD > 0) {
            msg("Dash cooldown!", Color.GRAY);
            return;
        }

        dashActive = true;
        dashTimer = 0.25f;

        float dashDistance = 170f;

        if (heroVx < 0) {
            hero.setPosition(
                Math.max(20, hero.getX() - dashDistance),
                hero.getY()
            );
        } else {
            hero.setPosition(
                Math.min(1220, hero.getX() + dashDistance),
                hero.getY()
            );
        }

        dashCD = DASH_CD;

        msg("⚡ DASH!", Color.CYAN);
    }
}
