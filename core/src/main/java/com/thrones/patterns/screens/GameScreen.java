package com.thrones.patterns.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.thrones.patterns.WarOfRealms;
import com.thrones.patterns.characters.Hero;
import com.thrones.patterns.enemies.Enemy;
import com.thrones.patterns.patterns.builder.BattleConfig;
import com.thrones.patterns.patterns.builder.BattleConfigBuilder;
import com.thrones.patterns.patterns.facade.BattleFacade;
import com.thrones.patterns.patterns.factory.HeroFactory;
import com.thrones.patterns.patterns.prototype.EnemyPrototypeRegistry;
import com.thrones.patterns.patterns.singleton.GameStateSingleton;
import com.thrones.patterns.utils.CharacterRenderer;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private final WarOfRealms game;
    private final BattleFacade facade;
    private final GameStateSingleton state;

    private Hero hero;
    private final List<Enemy> enemies = new ArrayList<>();

    private ShapeRenderer sr;
    private BitmapFont font;

    private Texture battleBackground;
    private Texture heroTex;
    private Texture goblinTex;
    private Texture whitewalkerTex;
    private Music battleMusic;
    private Sound swordSwingSound;
    private Sound swordHitSound;
    private Sound enemyDeathSound;
    private Sound fireSound;

    private CampaignLevel currentLevel;

    private int targetIndex = 0;
    private float time = 0f;

    private float attackCD = 0f;
    private float abilityCD = 0f;
    private float shieldCD = 0f;

    private boolean shieldActive = false;
    private float shieldTimer = 0f;

    private String message = "";
    private float msgTimer = 0f;
    private Color msgColor = Color.YELLOW;

    private boolean heroHit = false;
    private float heroHitTimer = 0f;

    private boolean[] enemyHit;
    private float[] enemyHitTimer;

    private float levelStartTimer = 0f;
    private boolean screenChanging = false;

    private static final float GROUND = 180f;
    private static final float GRAVITY = -1200f;
    private static final float HERO_SPEED = 300f;
    private static final float JUMP_FORCE = 550f;
    private static final float ENEMY_SPEED = 105f;

    private static final float ATTACK_CD = 0.4f;
    private static final float ABILITY_CD = 4f;
    private static final float SHIELD_CD = 6f;

    private float heroVx = 0f;
    private float heroVy = 0f;
    private boolean isGrounded = true;

    // Анимация айнымалылары
    private float attackAnimTimer = 0f;
    private boolean isAttacking = false;
    private float heroRunCycle = 0f;
    private float[] enemyShakeTimer;
    private float[] enemyDeathTimer;
    private boolean[] enemyDying;

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

        if (state.getLevel() < 1) state.setLevel(1);

        currentLevel = CampaignLevel.get(state.getLevel());
        loadAssets();
        setupLevel();
        initSnowfield(120);
    }

    private void loadAssets() {
        try {
            battleBackground = new Texture(currentLevel.background);
        } catch (Exception e) {
            try {
                battleBackground = new Texture("ui/backgrounds/battle_bg.png");
            } catch (Exception ignored) {
                battleBackground = null;
            }
        }

        try {
            battleMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/battle_theme.mp3"));
            battleMusic.setLooping(true);
            battleMusic.setVolume(0.25f);
            battleMusic.play();
        } catch (Exception e) { battleMusic = null; }

        try {
            swordSwingSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sword_swing.wav"));
        } catch (Exception e) { swordSwingSound = null; }

        try {
            swordHitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sword_hit.wav"));
        } catch (Exception e) { swordHitSound = null; }

        try {
            enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_die.wav"));
        } catch (Exception e) { enemyDeathSound = null; }

        try {
            fireSound = Gdx.audio.newSound(Gdx.files.internal("sounds/fire_spell.wav"));
        } catch (Exception e) { fireSound = null; }

        try {
            String heroType = state.getSelectedHeroType();
            if ("MAGE".equals(heroType)) {
                heroTex = new Texture(Gdx.files.internal("mage.png"));
            } else if ("ARCHER".equals(heroType)) {
                heroTex = new Texture(Gdx.files.internal("archer.png"));
            } else {
                heroTex = new Texture(Gdx.files.internal("knight.png"));
            }
        } catch (Exception e) { heroTex = null; }

        try {
            goblinTex = new Texture(Gdx.files.internal("goblin.png"));
        } catch (Exception e) { goblinTex = null; }

        try {
            whitewalkerTex = new Texture(Gdx.files.internal("whitewalker.png"));
        } catch (Exception e) { whitewalkerTex = null; }
    }

    private void setupLevel() {
        String heroType = state.getSelectedHeroType();
        if (heroType == null ||
            (!heroType.equals("KNIGHT") && !heroType.equals("MAGE") && !heroType.equals("ARCHER"))) {
            heroType = "KNIGHT";
            state.setSelectedHeroType("KNIGHT");
        }

        hero = HeroFactory.createHero(heroType, "Selected House");
        hero.setPosition(120, GROUND);
        enemies.clear();

        EnemyPrototypeRegistry registry = EnemyPrototypeRegistry.getInstance();
        for (String enemyType : currentLevel.enemies) {
            try {
                enemies.add(registry.spawn(enemyType));
            } catch (Exception e) {
                enemies.add(registry.spawn("GOBLIN"));
            }
        }

        float[] xs = {760, 930, 840, 1040, 1120, 1210};
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).setPosition(xs[Math.min(i, xs.length - 1)], GROUND);
            enemies.get(i).setTarget(hero);
        }

        int size = enemies.size();
        enemyHit = new boolean[size];
        enemyHitTimer = new float[size];
        enemyShakeTimer = new float[size];
        enemyDeathTimer = new float[size];
        enemyDying = new boolean[size];

        BattleConfig config = new BattleConfigBuilder()
            .setBattleName("Level " + state.getLevel() + ": " + currentLevel.title)
            .setHero(hero)
            .setWaveCount(state.getLevel())
            .build();
        facade.setupBattle(config);

        msg("LEVEL " + state.getLevel() + " — " + currentLevel.title, Color.GOLD);
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
        drawScene();
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
        heroVx = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) heroVx = -HERO_SPEED;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) heroVx = HERO_SPEED;

        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && isGrounded) {
            heroVy = JUMP_FORCE;
            isGrounded = false;
        }

        if (!isGrounded) heroVy += GRAVITY * delta;

        float nextX = hero.getX() + heroVx * delta;
        float nextY = hero.getY() + heroVy * delta;

        if (nextX < 0) nextX = 0;
        if (nextX > 1220) nextX = 1220;

        if (nextY <= GROUND) {
            nextY = GROUND;
            heroVy = 0f;
            isGrounded = true;
        }

        hero.setPosition(nextX, nextY);
        hero.update(delta);
    }

    private void updateEnemies(float delta) {
        float speedBonus = 1f + state.getLevel() * 0.07f;
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            float diffX = hero.getX() - enemy.getX();
            if (Math.abs(diffX) > 48f) {
                float dir = Math.signum(diffX);
                enemy.setPosition(
                    enemy.getX() + dir * ENEMY_SPEED * speedBonus * delta,
                    enemy.getY()
                );
            } else {
                if (!shieldActive) {
                    facade.enemyAttacks(enemy, hero);
                    heroHit = true;
                    heroHitTimer = 0.15f;
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

        if (shieldActive) {
            shieldTimer -= delta;
            if (shieldTimer <= 0) shieldActive = false;
        }

        if (heroHitTimer > 0) {
            heroHitTimer -= delta;
            heroHit = heroHitTimer > 0;
        }

        for (int i = 0; i < enemyHitTimer.length; i++) {
            if (enemyHitTimer[i] > 0) {
                enemyHitTimer[i] -= delta;
                enemyHit[i] = enemyHitTimer[i] > 0;
            }
        }

        // Шабуыл анимация таймері
        if (attackAnimTimer > 0) {
            attackAnimTimer -= delta;
            if (attackAnimTimer <= 0) isAttacking = false;
        }

        // Жүгіру циклі
        if (heroVx != 0) heroRunCycle += delta * 8f;
        else heroRunCycle = 0f;

        // Жау шайқалу таймері
        for (int i = 0; i < enemyShakeTimer.length; i++) {
            if (enemyShakeTimer[i] > 0) enemyShakeTimer[i] -= delta;
        }

        // Жау өлу таймері
        for (int i = 0; i < enemyDeathTimer.length; i++) {
            if (enemyDeathTimer[i] > 0) enemyDeathTimer[i] -= delta;
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()) {
            normalAttack();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) activateShield();
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) useSpecialAbility();

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) selectTarget(0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) selectTarget(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) selectTarget(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) selectTarget(3);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) selectTarget(4);

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            nextTarget();
            Enemy t = getTarget();
            if (t != null) msg("Target: " + t.getName(), Color.YELLOW);
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

        float distance = Math.abs(hero.getX() - target.getX());
        if (distance > 120f) {
            msg("Too far! Run closer!", Color.LIGHT_GRAY);
            return;
        }

        int index = enemies.indexOf(target);
        if (swordSwingSound != null) swordSwingSound.play(0.6f);

        float damage = hero.getAttack() * getWeaponBonus();
        target.takeDamage(damage);

        if (swordHitSound != null) swordHitSound.play(0.7f);

        rewardIfDead(target);
        attackCD = ATTACK_CD;

        // Шабуыл анимациясы
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
        msg("[S] Defensive shield active!", Color.CYAN);
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
                if (enemy.isAlive() && Math.abs(enemy.getX() - target.getX()) < 180f) {
                    enemy.takeDamage(damage);
                    rewardIfDead(enemy);
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

    private void rewardIfDead(Enemy enemy) {
        if (!enemy.isAlive()) {
            if (enemyDeathSound != null) enemyDeathSound.play(0.8f);
            state.addGold(enemy.getGoldReward());
            state.addScore(enemy.getExpReward() * 10);
            int idx = enemies.indexOf(enemy);
            if (idx >= 0 && idx < enemyDying.length) {
                enemyDying[idx] = true;
                enemyDeathTimer[idx] = 0.5f;
            }
        }
    }

    private float getWeaponBonus() { return 1f + state.getLevel() * 0.12f; }

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
            game.batch.setColor(0.55f, 0.55f, 0.6f, 1f);
            game.batch.draw(battleBackground, 0, 0, 1280, 720);
            game.batch.setColor(Color.WHITE);
        }
        game.batch.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.35f);
        sr.rect(0, 0, 1280, 720);
        sr.end();
    }

    private void drawScene() {
        sr.begin(ShapeRenderer.ShapeType.Filled);

        sr.setColor(0.10f, 0.065f, 0.035f, 1f);
        sr.rect(0, 0, 1280, GROUND);
        sr.setColor(0.32f, 0.23f, 0.12f, 1f);
        sr.rect(0, GROUND - 4, 1280, 8);

        sr.setColor(0.045f, 0.035f, 0.07f, 1f);
        sr.rect(75, GROUND, 80, 190);
        sr.rect(155, GROUND, 120, 120);
        sr.rect(225, GROUND, 60, 160);
        sr.rect(910, GROUND, 85, 175);
        sr.rect(995, GROUND, 120, 115);
        sr.rect(1065, GROUND, 55, 145);

        if (state.getLevel() >= 3) {
            sr.setColor(0.12f, 0.12f, 0.14f, 1f);
            sr.circle(430, GROUND + 18, 24);
            sr.circle(455, GROUND + 14, 18);
        }
        if (state.getLevel() >= 5) {
            sr.setColor(0.10f, 0.06f, 0.03f, 1f);
            sr.rect(650, GROUND, 12, 90);
            sr.rectLine(656, GROUND + 70, 610, GROUND + 110, 5);
            sr.rectLine(656, GROUND + 55, 700, GROUND + 95, 5);
        }
        if (state.getLevel() >= 7) {
            sr.setColor(0.09f, 0.09f, 0.11f, 1f);
            sr.triangle(820, GROUND, 900, GROUND, 860, GROUND + 95);
            sr.triangle(870, GROUND, 950, GROUND, 910, GROUND + 125);
        }

        sr.setColor(0.25f, 0.02f, 0.02f, 0.25f);
        sr.rect(0, GROUND, 1280, 45);
        sr.end();
    }

    private void drawCharacters() {
        game.batch.begin();

        // ── ГЕРОЙ АНИМАЦИЯСЫ ──
        float bobY = 0f;
        float tiltAngle = 0f;
        float scaleW = 1f;
        float scaleH = 1f;
        float attackOffsetX = 0f;
        float attackOffsetY = 0f;
        float breathe = (float)(Math.sin(time * 1.5f) * 2f);

        // Жүгіру
        if (heroVx != 0 && isGrounded) {
            bobY = (float)(Math.sin(heroRunCycle) * 6f);
            tiltAngle = heroVx > 0 ? -12f : 12f;
            scaleH = 1f + (float)(Math.sin(heroRunCycle * 2f) * 0.06f);
            scaleW = 1f - (float)(Math.sin(heroRunCycle * 2f) * 0.04f);
        }

        // Шабуыл
        if (isAttacking) {
            float progress = 1f - (attackAnimTimer / 0.3f);
            attackOffsetX = (float)(Math.sin(progress * Math.PI)) * 30f;
            attackOffsetY = (float)(Math.sin(progress * Math.PI)) * 10f;
            tiltAngle = (float)(Math.sin(progress * Math.PI)) * -25f;
            scaleH = 1f + (float)(Math.sin(progress * Math.PI)) * 0.1f;
        }

        // Секіру
        if (!isGrounded) {
            scaleH = heroVy > 0 ? 1.15f : 0.9f;
            scaleW = heroVy > 0 ? 0.9f : 1.1f;
            tiltAngle = heroVy > 0 ? -5f : 5f;
        }

        float drawX = hero.getX() - 10 + attackOffsetX;
        float drawY = hero.getY() + bobY + attackOffsetY + breathe;

        if (heroTex != null) {
            if (heroHit) {
                float flicker = (float)(Math.sin(time * 30f) * 0.5f + 0.5f);
                game.batch.setColor(1f, flicker * 0.4f, flicker * 0.4f, 1f);
            } else {
                game.batch.setColor(Color.WHITE);
            }

            game.batch.draw(
                heroTex,
                drawX, drawY,
                40f, 0f,
                80f * scaleW, 140f * scaleH,
                1f, 1f,
                tiltAngle,
                0, 0,
                heroTex.getWidth(), heroTex.getHeight(),
                false, false
            );
            game.batch.setColor(Color.WHITE);
        } else {
            game.batch.end();
            if (hero.getType().equals("MAGE")) {
                CharacterRenderer.drawMage(sr, drawX, drawY, 1f, time, heroHit);
            } else if (hero.getType().equals("ARCHER")) {
                CharacterRenderer.drawArcher(sr, drawX, drawY, 1f, time, heroHit);
            } else {
                CharacterRenderer.drawKnight(sr, drawX, drawY, 1f, time, heroHit);
            }
            game.batch.begin();
        }

        // Шабуыл кезінде қылыш жарқылы
        if (isAttacking) {
            game.batch.end();
            float progress = 1f - (attackAnimTimer / 0.3f);
            float alpha = (float)(Math.sin(progress * Math.PI));
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(1f, 0.9f, 0.3f, alpha * 0.8f);
            float sx = drawX + 80f + 10;
            float sy = drawY + 70f;
            sr.rectLine(sx, sy, sx + 55f * alpha, sy - 25f * alpha, 5f);
            sr.rectLine(sx, sy, sx + 45f * alpha, sy + 15f * alpha, 3f);
            sr.end();
            game.batch.begin();
        }

        // ── ЖАУЛАР АНИМАЦИЯСЫ ──
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);

            // Өлу анимациясы
            if (!enemy.isAlive()) {
                if (i < enemyDying.length && enemyDying[i] && enemyDeathTimer[i] > 0) {
                    float progress = 1f - (enemyDeathTimer[i] / 0.5f);
                    float fallY = enemy.getY() - progress * 30f;
                    float alpha = enemyDeathTimer[i] / 0.5f;
                    Texture dTex = enemy.getType().equals("GOBLIN") ? goblinTex : whitewalkerTex;
                    if (dTex != null) {
                        game.batch.setColor(1f, 1f, 1f, alpha);
                        game.batch.draw(dTex, enemy.getX() - 5, fallY,
                            70, 110 * (1f - progress * 0.3f));
                        game.batch.setColor(Color.WHITE);
                    }
                }
                continue;
            }

            boolean hit = i < enemyHit.length && enemyHit[i];

            // Шайқалу анимациясы
            float shakeX = 0f;
            if (i < enemyShakeTimer.length && enemyShakeTimer[i] > 0) {
                shakeX = (float)(Math.sin(enemyShakeTimer[i] * 40f) * 6f);
            }

            // Жүру боб
            float enemyBob = (float)(Math.sin(time * 4f + i) * 3f);

            // Жау да алға еңкейеді (жүру иллюзиясы)
            float enemyTilt = -8f;

            Texture enemyTex = null;
            float w = 70, h = 110;

            switch (enemy.getType()) {
                case "GOBLIN":
                    enemyTex = goblinTex; w = 60; h = 95; break;
                case "DARK_KNIGHT": case "NECROMANCER": case "ORC":
                    enemyTex = whitewalkerTex; w = 75; h = 120; break;
                case "DRAGON":
                    enemyTex = whitewalkerTex; w = 110; h = 130; break;
                default:
                    enemyTex = goblinTex; break;
            }

            if (enemyTex != null) {
                if (hit) game.batch.setColor(1f, 0.3f, 0.3f, 1f);
                else game.batch.setColor(Color.WHITE);

                game.batch.draw(
                    enemyTex,
                    enemy.getX() - 5 + shakeX,
                    enemy.getY() + enemyBob,
                    w / 2, 0f,
                    w, h,
                    1f, 1f,
                    enemyTilt,
                    0, 0,
                    enemyTex.getWidth(), enemyTex.getHeight(),
                    false, false
                );
                game.batch.setColor(Color.WHITE);
            } else {
                game.batch.end();
                switch (enemy.getType()) {
                    case "GOBLIN":
                        CharacterRenderer.drawGoblin(sr, enemy.getX() + shakeX,
                            enemy.getY(), 0.9f, time, hit); break;
                    case "ORC":
                        CharacterRenderer.drawOrc(sr, enemy.getX() + shakeX,
                            enemy.getY(), 0.85f, time, hit); break;
                    case "DARK_KNIGHT":
                        CharacterRenderer.drawDarkKnight(sr, enemy.getX() + shakeX,
                            enemy.getY(), 0.95f, time, hit); break;
                    case "NECROMANCER":
                        CharacterRenderer.drawNecromancer(sr, enemy.getX() + shakeX,
                            enemy.getY(), 0.9f, time, hit); break;
                    case "DRAGON":
                        CharacterRenderer.drawDragon(sr, enemy.getX() - 20 + shakeX,
                            enemy.getY(), 0.8f, time, hit); break;
                }
                game.batch.begin();
            }
        }

        game.batch.end();

        // Shield эффект
        if (shieldActive) {
            sr.begin(ShapeRenderer.ShapeType.Line);
            float pulse = (float)(Math.sin(time * 10f) * 0.5f + 0.5f);
            sr.setColor(0f, pulse, pulse, 1f);
            sr.circle(hero.getX() + 24, hero.getY() + 36, 40, 20);
            sr.end();
        }
    }

    private void drawHUD() {
        font.getData().setScale(1f);
        font.setColor(Color.GOLD);
        font.draw(game.batch, "GAME OF THRONES: THE LAST STAND", 365, 714);

        font.getData().setScale(0.85f);
        font.setColor(Color.WHITE);
        font.draw(game.batch, "Level: " + state.getLevel() + "/9", 20, 695);
        font.setColor(Color.GOLD);
        font.draw(game.batch, currentLevel.title, 20, 672);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(game.batch, currentLevel.objective, 20, 649);
        font.setColor(Color.YELLOW);
        font.draw(game.batch, "Gold: " + state.getGold(), 20, 626);
        font.setColor(Color.CYAN);
        font.draw(game.batch, "Score: " + state.getScore(), 20, 603);
        font.setColor(Color.WHITE);
        font.draw(game.batch, hero.getName() + " Lv." + hero.getLevel(), 20, 580);
        font.setColor(Color.ORANGE);
        font.draw(game.batch, "Weapon: " + getWeaponName() + " x" +
            String.format("%.2f", getWeaponBonus()), 20, 557);

        float hpPct = hero.getHp() / hero.getMaxHp();
        font.setColor(hpPct > 0.5f ? Color.GREEN : hpPct > 0.25f ? Color.ORANGE : Color.RED);
        font.draw(game.batch, "HP: " + (int)hero.getHp() + "/" + (int)hero.getMaxHp(), 20, 534);

        if (shieldActive) {
            font.setColor(Color.CYAN);
            font.draw(game.batch, "SHIELD ACTIVE!", 20, 511);
        }

        font.setColor(Color.LIGHT_GRAY);
        font.draw(game.batch, "ENEMY REALM:", 1030, 695);
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            font.setColor(i == targetIndex && enemy.isAlive() ? Color.YELLOW :
                enemy.isAlive() ? Color.WHITE : Color.DARK_GRAY);
            font.draw(game.batch,
                (i+1) + ". " + enemy.getName() +
                    (enemy.isAlive() ? " HP: " + (int)enemy.getHp() : " [DEAD]"),
                1030, 672 - i * 22);
        }
        font.getData().setScale(1f);
    }

    private void drawControls() {
        font.getData().setScale(0.82f);
        int y = 155, s = 20;

        font.setColor(Color.WHITE);
        font.draw(game.batch, "[A] Run Left  |  [D] Run Right  |  [W] Jump", 20, y);
        font.setColor(attackCD > 0 ? Color.GRAY : Color.GREEN);
        font.draw(game.batch, "[SPACE] Sword Attack" +
            (attackCD > 0 ? " (Cooling)" : " [READY]"), 20, y - s);

        cd("[S]", "Defensive Shield", shieldCD, Color.CYAN, y - s * 2);
        cd("[E]", "House Special Ability", abilityCD, Color.ORANGE, y - s * 3);

        font.setColor(Color.DARK_GRAY);
        font.draw(game.batch, "[1-5] Target  |  [TAB] Next  |  [ESC] Menu",
            20, y - s * 4 - 4);
        font.getData().setScale(1f);
    }

    private void cd(String key, String name, float cd, Color color, float y) {
        if (cd > 0) {
            font.setColor(Color.GRAY);
            font.draw(game.batch, key + " " + name + " (" + (int)cd + "s)", 20, y);
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

        float hp = hero.getHp() / hero.getMaxHp();
        sr.setColor(0.15f, 0.15f, 0.15f, 1f);
        sr.rect(hero.getX(), hero.getY() + 80, 80, 10);
        sr.setColor(hp > 0.5f ? Color.GREEN : hp > 0.25f ? Color.ORANGE : Color.RED);
        sr.rect(hero.getX(), hero.getY() + 80, 80 * hp, 10);

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            float ep = enemy.getHp() / enemy.getMaxHp();
            sr.setColor(0.15f, 0.15f, 0.15f, 1f);
            sr.rect(enemy.getX(), enemy.getY() + 78, 64, 8);
            sr.setColor(Color.RED);
            sr.rect(enemy.getX(), enemy.getY() + 78, 64 * ep, 8);
        }
        sr.end();
    }

    private void drawTarget() {
        if (targetIndex < enemies.size() && enemies.get(targetIndex).isAlive()) {
            Enemy target = enemies.get(targetIndex);
            float pulse = (float)(Math.sin(time * 4f) * 0.4f + 0.6f);
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(pulse, pulse * 0.8f, 0f, 1f);
            sr.rect(target.getX() - 4, target.getY() - 4, 72, 90);
            sr.triangle(target.getX() + 28, target.getY() + 95,
                target.getX() + 36, target.getY() + 110,
                target.getX() + 44, target.getY() + 95);
            sr.end();
        }
    }

    private void checkLevelEnd(float delta) {
        if (screenChanging) return;
        levelStartTimer += delta;
        if (levelStartTimer <= 1.5f) return;

        if (facade.isBattleOver(hero, enemies)) {
            screenChanging = true;
            stopMusic();
            if (state.isGameOver() || !hero.isAlive()) {
                game.setScreen(new GameOverScreen(game));
                return;
            }
            if (state.getLevel() >= 9) {
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
            if (enemies.get(next).isAlive()) { targetIndex = next; return; }
        }
    }

    private Enemy getTarget() {
        if (targetIndex < enemies.size() && enemies.get(targetIndex).isAlive())
            return enemies.get(targetIndex);
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).isAlive()) { targetIndex = i; return enemies.get(i); }
        }
        return null;
    }

    private void msg(String text, Color color) {
        message = text; msgTimer = 2.5f; msgColor = color;
    }

    private void stopMusic() {
        if (battleMusic != null) battleMusic.stop();
    }

    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() { stopMusic(); }

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
        if (heroTex != null) heroTex.dispose();
        if (goblinTex != null) goblinTex.dispose();
        if (whitewalkerTex != null) whitewalkerTex.dispose();
    }

    // ═══════════════════════════════════════
    // CAMPAIGN LEVELS
    // ═══════════════════════════════════════
    private static class CampaignLevel {
        int level;
        String title, objective, background;
        String[] enemies;

        CampaignLevel(int level, String title, String objective,
                      String background, String[] enemies) {
            this.level = level; this.title = title;
            this.objective = objective; this.background = background;
            this.enemies = enemies;
        }

        static CampaignLevel get(int level) {
            switch (level) {
                case 1: return new CampaignLevel(1, "Border Ambush",
                    "Defeat the goblin scouts.", "ui/backgrounds/level1.png",
                    new String[]{"GOBLIN", "GOBLIN"});
                case 2: return new CampaignLevel(2, "Burned Village",
                    "Survive the orc ambush.", "ui/backgrounds/level2.png",
                    new String[]{"GOBLIN", "GOBLIN", "ORC"});
                case 3: return new CampaignLevel(3, "Castle Gate",
                    "Break through the first royal guards.", "ui/backgrounds/level3.png",
                    new String[]{"GOBLIN", "ORC", "DARK_KNIGHT"});
                case 4: return new CampaignLevel(4, "Hall of Betrayal",
                    "Kill the knight who betrayed your bloodline.", "ui/backgrounds/level4.png",
                    new String[]{"ORC", "DARK_KNIGHT", "DARK_KNIGHT"});
                case 5: return new CampaignLevel(5, "Siege of Ironkeep",
                    "Defeat the fortress army.", "ui/backgrounds/level5.png",
                    new String[]{"GOBLIN", "ORC", "ORC", "DARK_KNIGHT"});
                case 6: return new CampaignLevel(6, "Necromancer Crypt",
                    "Stop the dead army from rising.", "ui/backgrounds/level6.png",
                    new String[]{"ORC", "DARK_KNIGHT", "NECROMANCER"});
                case 7: return new CampaignLevel(7, "Dragon Valley",
                    "Face the first dragon.", "ui/backgrounds/level7.png",
                    new String[]{"ORC", "DARK_KNIGHT", "DRAGON"});
                case 8: return new CampaignLevel(8, "War of Five Houses",
                    "Survive the united enemy houses.", "ui/backgrounds/level8.png",
                    new String[]{"GOBLIN", "ORC", "DARK_KNIGHT", "NECROMANCER", "DRAGON"});
                case 9: default: return new CampaignLevel(9, "The Black Throne",
                    "Defeat the ancient dragons and claim the throne.",
                    "ui/backgrounds/level9.png",
                    new String[]{"DARK_KNIGHT", "NECROMANCER", "DRAGON", "DRAGON"});
            }
        }
    }

    // ═══════════════════════════════════════
    // SNOWFLAKES
    // ═══════════════════════════════════════
    private static class Snowflake {
        float x, y, speed, size, alpha;
        boolean fading;
    }

    private void initSnowfield(int count) {
        snowflakesList = new ArrayList<>();
        for (int i = 0; i < count; i++) snowflakesList.add(createSnowflake(true));
    }

    private Snowflake createSnowflake(boolean randomY) {
        Snowflake s = new Snowflake();
        s.x = MathUtils.random(0, 1280);
        s.y = randomY ? MathUtils.random(0, 720) : 720;
        s.speed = MathUtils.random(150f, 350f);
        s.size = MathUtils.random(1.5f, 3.5f);
        s.alpha = MathUtils.random(0.3f, 0.9f);
        s.fading = MathUtils.randomBoolean();
        return s;
    }

    private void updateSnowfield(float delta) {
        if (snowflakesList == null) return;
        float mult = 1f + state.getLevel() * 0.15f;
        for (int i = 0; i < snowflakesList.size(); i++) {
            Snowflake s = snowflakesList.get(i);
            s.y -= s.speed * delta * mult;
            s.x -= s.speed * 0.3f * delta;
            if (s.fading) { s.alpha -= delta * 0.5f; if (s.alpha <= 0.2f) s.fading = false; }
            else { s.alpha += delta * 0.5f; if (s.alpha >= 0.9f) s.fading = true; }
            if (s.y < 0 || s.x < 0) snowflakesList.set(i, createSnowflake(false));
        }
    }

    private void renderSnowfield(ShapeRenderer renderer) {
        if (snowflakesList == null) return;
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Snowflake s : snowflakesList) {
            renderer.setColor(0.85f, 0.95f, 1f, s.alpha);
            renderer.circle(s.x, s.y, s.size);
        }
        renderer.end();
    }
}
