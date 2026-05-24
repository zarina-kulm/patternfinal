package com.thrones.patterns.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.thrones.patterns.WarOfRealms;
import com.thrones.patterns.characters.Hero;
import com.thrones.patterns.enemies.Enemy;
import com.thrones.patterns.patterns.builder.*;
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
    private List<Enemy> enemies;
    private int targetIndex = 0;

    private ShapeRenderer sr;
    private BitmapFont font;

    private float time = 0;
    private float fireCD = 0, tauntCD = 0, normalAttackCD = 0;
    private boolean dodgeActive = false;
    private float dodgeTimer = 0;

    private String message = "";
    private float msgTimer = 0;
    private Color msgColor = Color.YELLOW;

    private boolean heroHit = false;
    private float heroHitTimer = 0;
    private boolean[] eHit;
    private float[] eHitTimer;

    private float waveStartTimer = 0f;
    private boolean screenChanging = false;

    // --- ФИЗИКА ДВИЖЕНИЯ И ПРЫЖКОВ ---
    private static final float GROUND = 180f;
    private float heroVx = 0f;
    private float heroVy = 0f;
    private boolean isGrounded = true;
    private static final float GRAVITY = -1200f; // Сила притяжения
    private static final float SPEED = 300f;     // Скорость бега персонажа
    private static final float JUMP_FORCE = 550f; // Сила прыжка
    private static final float ENEMY_SPEED = 120f; // Скорость бега врагов

    private static final float NORMAL_ATTACK_SPEED = 0.4f;
    private static final float FIRE_CD = 4f;
    private static final float TAUNT_CD = 6f;

    public GameScreen(WarOfRealms game) {
        this.game = game;
        this.facade = new BattleFacade();
        this.state = GameStateSingleton.getInstance();
        this.enemies = new ArrayList<>();
        this.sr = new ShapeRenderer();
        this.font = new BitmapFont();
        setup();
    }

    private void setup() {
        hero = HeroFactory.createHero(state.getSelectedHeroType(), "House of Players");
        hero.setPosition(120, GROUND);

        EnemyPrototypeRegistry reg = EnemyPrototypeRegistry.getInstance();
        enemies.add(reg.spawn("GOBLIN"));
        enemies.add(reg.spawn("GOBLIN"));
        if (state.getWave() >= 2) enemies.add(reg.spawn("ORC"));
        if (state.getWave() >= 3) enemies.add(reg.spawn("DARK_KNIGHT"));
        if (state.getWave() >= 4) enemies.add(reg.spawn("NECROMANCER"));
        if (state.getWave() >= 5) enemies.add(reg.spawn("DRAGON"));

        float[] xs = {750, 950, 850, 1050, 1150, 1250};
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).setPosition(xs[Math.min(i, xs.length - 1)], GROUND);
            enemies.get(i).setTarget(hero);
        }

        eHit = new boolean[enemies.size()];
        eHitTimer = new float[enemies.size()];

        BattleConfig cfg = new BattleConfigBuilder()
            .setBattleName("Wave " + state.getWave())
            .setHero(hero)
            .setWaveCount(state.getWave())
            .build();
        facade.setupBattle(cfg);

        initSnowfield(120);
        msg("Wave " + state.getWave() + " — DEFEND WINTERFELL!", Color.GOLD);
    }

    private void msg(String m, Color c) { message = m; msgTimer = 2.5f; msgColor = c; }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        time += delta;
        update(delta);

        updateSnowfield(delta);
        renderSnowfield(sr);

        drawScene();
        drawCharacters();

        game.batch.begin();
        drawHUD();
        drawControls();
        if (msgTimer > 0) {
            font.getData().setScale(1.4f);
            font.setColor(msgColor.r, msgColor.g, msgColor.b, Math.min(1f, msgTimer));
            font.draw(game.batch, message, 400, 420);
            font.getData().setScale(1f);
        }
        game.batch.end();

        drawHPBars();
        drawTarget();

        if (!screenChanging) {
            waveStartTimer += delta;
            if (waveStartTimer > 1.5f) {
                if (facade.isBattleOver(hero, enemies)) {
                    screenChanging = true;
                    if (state.isGameOver() || !hero.isAlive()) {
                        game.setScreen(new GameOverScreen(game));
                    } else {
                        game.setScreen(new VictoryScreen(game));
                    }
                }
            }
        }
    }

    private void update(float delta) {
        // --- РЕАЛ-ТАЙМ ФИЗИКА ДЛЯ ГЛАВНОГО ПЕРСОНАЖА ---
        heroVx = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            heroVx = -SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            heroVx = SPEED;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && isGrounded) {
            heroVy = JUMP_FORCE;
            isGrounded = false;
        }

        // Применяем гравитацию
        if (!isGrounded) {
            heroVy += GRAVITY * delta;
        }

        // Обновляем координаты героя
        float nextX = hero.getX() + heroVx * delta;
        float nextY = hero.getY() + heroVy * delta;

        // Ограничения экрана
        if (nextX < 0) nextX = 0;
        if (nextX > 1220) nextX = 1220;

        // Проверка приземления на землю
        if (nextY <= GROUND) {
            nextY = GROUND;
            heroVy = 0;
            isGrounded = true;
        }

        hero.setPosition(nextX, nextY);
        hero.update(delta);

        // --- ИИ ВРАГОВ: БЕГУТ ЗА ГЕРОЕМ И БЬЮТ ТОЛЬКО ВБЛИЗИ ---
        for (Enemy e : enemies) {
            if (!e.isAlive()) continue;

            // Вычисляем расстояние до героя по оси X
            float diffX = hero.getX() - e.getX();

            if (Math.abs(diffX) > 45f) {
                // Если герой далеко — враг бежит к нему
                float direction = Math.signum(diffX);
                e.setPosition(e.getX() + direction * ENEMY_SPEED * delta, e.getY());
            } else {
                // Если подошел в упор — атакует (если на герое нет активного щита)
                if (!dodgeActive) {
                    facade.enemyAttacks(e, hero);
                }
            }
            e.update(delta);
        }

        // Кулдауны умений
        if (fireCD > 0) fireCD -= delta;
        if (tauntCD > 0) tauntCD -= delta;
        if (normalAttackCD > 0) normalAttackCD -= delta;
        if (msgTimer > 0) msgTimer -= delta;
        if (dodgeActive) { dodgeTimer -= delta; if (dodgeTimer <= 0) dodgeActive = false; }
        if (heroHitTimer > 0) { heroHitTimer -= delta; heroHit = heroHitTimer > 0; }
        for (int i = 0; i < eHitTimer.length; i++) {
            if (eHitTimer[i] > 0) { eHitTimer[i] -= delta; eHit[i] = eHitTimer[i] > 0; }
        }

        handleInput();
    }

    private void handleInput() {
        // [КЛИК МЫШКИ ИЛИ КЛАВИША SPACE] — СРАЖАТЬСЯ ВБЛИЗИ
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()) {
            if (normalAttackCD <= 0) {
                Enemy t = getTarget();
                if (t != null) {
                    // Проверяем, достает ли меч до врага (дистанция атаки)
                    float dist = Math.abs(hero.getX() - t.getX());
                    if (dist <= 120f) {
                        int idx = enemies.indexOf(t);
                        facade.heroAttacks(hero, t);
                        normalAttackCD = NORMAL_ATTACK_SPEED;
                        if (idx >= 0 && idx < eHit.length) {
                            eHit[idx] = true;
                            eHitTimer[idx] = 0.15f;
                        }
                        msg("Hit " + t.getName() + "!", Color.WHITE);
                    } else {
                        msg("Too far! Run closer!", Color.LIGHT_GRAY);
                    }
                }
            }
        }

        // [S] — БЛОК / АКТИВАЦИЯ ЩИТА НА 2 СЕКУНДЫ
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (tauntCD <= 0) {
                dodgeActive = true;
                dodgeTimer = 2f;
                tauntCD = TAUNT_CD;
                msg("[S] DEFENSIVE SHIELD ACTIVE!", Color.CYAN);
            } else msg("Shield cooldown: " + (int) tauntCD + "s", Color.GRAY);
        }

        // [E] — ДРАКАРИС / МАГИЧЕСКИЙ ВЗРЫВ (БЬЕТ НА ДИСТАНЦИИ)
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (fireCD <= 0) {
                Enemy t = getTarget();
                if (t != null) {
                    float d = hero.getAttack() * 2.2f;
                    t.takeDamage(d);
                    msg("[E] Dragon Fire Explosion! -" + (int) d + " DMG", Color.ORANGE);
                }
                fireCD = FIRE_CD;
            } else msg("Ability cooldown: " + (int) fireCD + "s", Color.GRAY);
        }

        // Выбор целей кнопками
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) sel(0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) sel(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) sel(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) sel(3);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) sel(4);

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            nextTarget();
            Enemy t = getTarget();
            if (t != null) msg("Target: " + t.getName(), Color.YELLOW);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            game.setScreen(new MainMenuScreen(game));
    }

    private void sel(int i) {
        if (i < enemies.size() && enemies.get(i).isAlive()) {
            targetIndex = i; msg("Target: " + enemies.get(i).getName(), Color.YELLOW);
        }
    }

    private void nextTarget() {
        for (int i = 1; i <= enemies.size(); i++) {
            int n = (targetIndex + i) % enemies.size();
            if (enemies.get(n).isAlive()) { targetIndex = n; return; }
        }
    }

    private Enemy getTarget() {
        if (targetIndex < enemies.size() && enemies.get(targetIndex).isAlive())
            return enemies.get(targetIndex);
        for (int i = 0; i < enemies.size(); i++)
            if (enemies.get(i).isAlive()) { targetIndex = i; return enemies.get(i); }
        return null;
    }

    private void drawScene() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.12f, 0.08f, 0.04f, 1f);
        sr.rect(0, 0, 1280, GROUND);
        sr.setColor(0.2f, 0.15f, 0.08f, 1f);
        sr.rect(0, GROUND - 4, 1280, 8);

        sr.setColor(0.06f, 0.04f, 0.1f, 1f);
        sr.rect(100, GROUND, 60, 180); sr.rect(220, GROUND, 40, 140);
        sr.rect(900, GROUND, 60, 160); sr.rect(1050, GROUND, 50, 130);
        sr.rect(100, GROUND + 120, 160, 30); sr.rect(900, GROUND + 100, 160, 25);
        for (int i = 0; i < 4; i++) sr.rect(100 + i * 16, GROUND + 176, 10, 14);
        sr.end();
    }

    private void drawCharacters() {
        switch (hero.getType()) {
            case "KNIGHT": CharacterRenderer.drawKnight(sr, hero.getX(), hero.getY(), 1f, time, heroHit); break;
            case "MAGE":   CharacterRenderer.drawMage(sr, hero.getX(), hero.getY(), 1f, time, heroHit); break;
            case "ARCHER": CharacterRenderer.drawArcher(sr, hero.getX(), hero.getY(), 1f, time, heroHit); break;
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            if (!e.isAlive()) continue;
            boolean h = i < eHit.length && eHit[i];
            switch (e.getType()) {
                case "GOBLIN":      CharacterRenderer.drawGoblin(sr, e.getX(), e.getY(), 0.9f, time, h); break;
                case "ORC":         CharacterRenderer.drawOrc(sr, e.getX(), e.getY(), 0.85f, time, h); break;
                case "DARK_KNIGHT": CharacterRenderer.drawDarkKnight(sr, e.getX(), e.getY(), 0.95f, time, h); break;
                case "NECROMANCER": CharacterRenderer.drawNecromancer(sr, e.getX(), e.getY(), 0.9f, time, h); break;
                case "DRAGON":      CharacterRenderer.drawDragon(sr, e.getX() - 20, e.getY(), 0.8f, time, h); break;
            }
        }

        if (dodgeActive) {
            sr.begin(ShapeRenderer.ShapeType.Line);
            float p = (float) (Math.sin(time * 10f) * 0.5 + 0.5);
            sr.setColor(0f, p, p, 1f);
            sr.circle(hero.getX() + 24, hero.getY() + 36, 40, 20);
            sr.end();
        }
    }

    private void drawHUD() {
        font.getData().setScale(1f);
        font.setColor(Color.GOLD);
        font.draw(game.batch, "GAME OF THRONES: THE LAST STAND", 430, 714);
        font.getData().setScale(0.9f);
        font.setColor(Color.WHITE);
        font.draw(game.batch, "Wave: " + state.getWave() + "/5", 20, 695);
        font.setColor(Color.YELLOW);
        font.draw(game.batch, "Gold: " + state.getGold(), 20, 672);
        font.setColor(Color.CYAN);
        font.draw(game.batch, "Score: " + state.getScore(), 20, 649);
        font.setColor(Color.WHITE);
        font.draw(game.batch, hero.getName() + " Lv." + hero.getLevel(), 20, 626);
        float hPct = hero.getHp() / hero.getMaxHp();
        font.setColor(hPct > 0.5f ? Color.GREEN : hPct > 0.25f ? Color.ORANGE : Color.RED);
        font.draw(game.batch, "HP: " + (int) hero.getHp() + "/" + (int) hero.getMaxHp(), 20, 603);
        if (dodgeActive) { font.setColor(Color.CYAN); font.draw(game.batch, "SHIELD ACTIVE!", 20, 580); }

        font.setColor(Color.LIGHT_GRAY);
        font.draw(game.batch, "WHITE WALKERS ARMY:", 1030, 695);
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            font.setColor(i == targetIndex && e.isAlive() ? Color.YELLOW : e.isAlive() ? Color.WHITE : Color.DARK_GRAY);
            font.draw(game.batch, (i + 1) + ". " + e.getName() + (e.isAlive() ? " HP: " + (int) e.getHp() : " [DEAD]"), 1030, 672 - i * 22);
        }
        font.getData().setScale(1f);
    }

    private void drawControls() {
        font.getData().setScale(0.82f);
        int y = 155, s = 20;

        font.setColor(Color.WHITE);
        font.draw(game.batch, "[A] Run Left  |  [D] Run Right  |  [W] JUMP!", 20, y);

        if (normalAttackCD > 0) {
            font.setColor(Color.GRAY);
            font.draw(game.batch, "[SPACE / CLICK] Melee Attack (Cooling)", 20, y - s);
        } else {
            font.setColor(Color.GREEN);
            font.draw(game.batch, "[SPACE / CLICK] Melee Attack [READY]", 20, y - s);
        }

        cd("[S]", "Defensive Shield", tauntCD, Color.CYAN, y - s * 2);
        cd("[E]", "Dragon Fire Explosion (Ranged)", fireCD, Color.ORANGE, y - s * 3);

        font.setColor(Color.DARK_GRAY);
        font.draw(game.batch, "[1-5] Choose Target  |  [TAB] Next Target  |  [ESC] Main Menu", 20, y - s * 4 - 4);
        font.getData().setScale(1f);
    }

    private void cd(String key, String name, float c, Color col, float y) {
        if (c > 0) { font.setColor(Color.GRAY); font.draw(game.batch, key + " " + name + " (" + (int) c + "s)", 20, y); }
        else { font.setColor(col); font.draw(game.batch, key + " " + name + " [READY]", 20, y); }
    }

    private void drawHPBars() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        float hp = hero.getHp() / hero.getMaxHp();
        sr.setColor(0.15f, 0.15f, 0.15f, 1f);
        sr.rect(hero.getX(), hero.getY() + 80, 80, 10);
        sr.setColor(hp > 0.5f ? Color.GREEN : hp > 0.25f ? Color.ORANGE : Color.RED);
        sr.rect(hero.getX(), hero.getY() + 80, 80 * hp, 10);
        for (Enemy e : enemies) {
            if (!e.isAlive()) continue;
            float ep = e.getHp() / e.getMaxHp();
            sr.setColor(0.15f, 0.15f, 0.15f, 1f);
            sr.rect(e.getX(), e.getY() + 78, 64, 8);
            sr.setColor(Color.RED);
            sr.rect(e.getX(), e.getY() + 78, 64 * ep, 8);
        }
        sr.end();
    }

    private void drawTarget() {
        if (targetIndex < enemies.size() && enemies.get(targetIndex).isAlive()) {
            Enemy t = enemies.get(targetIndex);
            float p = (float) (Math.sin(time * 4f) * 0.4 + 0.6);
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(p, p * 0.8f, 0f, 1f);
            sr.rect(t.getX() - 4, t.getY() - 4, 72, 90);
            sr.triangle(t.getX() + 28, t.getY() + 95, t.getX() + 36, t.getY() + 110, t.getX() + 44, t.getY() + 95);
            sr.end();
        }
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { sr.dispose(); font.dispose(); }

    // --- СНЕЖНАЯ МЕТЕЛЬ НА ЗАДНЕМ ФОНЕ ---
    private static class Snowflake {
        float x, y, speed, size, alpha;
        boolean fading;
    }

    private List<Snowflake> snowflakesList;

    private void initSnowfield(int count) {
        snowflakesList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            snowflakesList.add(createSnowflake(true));
        }
    }

    private Snowflake createSnowflake(boolean randomY) {
        Snowflake s = new Snowflake();
        s.x = com.badlogic.gdx.math.MathUtils.random(0, 1280);
        s.y = randomY ? com.badlogic.gdx.math.MathUtils.random(0, 720) : 720;
        s.speed = com.badlogic.gdx.math.MathUtils.random(150f, 350f);
        s.size = com.badlogic.gdx.math.MathUtils.random(1.5f, 3.5f);
        s.alpha = com.badlogic.gdx.math.MathUtils.random(0.3f, 0.9f);
        s.fading = com.badlogic.gdx.math.MathUtils.randomBoolean();
        return s;
    }

    private void updateSnowfield(float delta) {
        if (snowflakesList == null) return;
        float waveMultiplier = 1f + (state.getWave() * 0.25f);

        for (int i = 0; i < snowflakesList.size(); i++) {
            Snowflake s = snowflakesList.get(i);
            s.y -= s.speed * delta * waveMultiplier;
            s.x -= (s.speed * 0.3f) * delta;

            if (s.fading) {
                s.alpha -= delta * 0.5f;
                if (s.alpha <= 0.2f) s.fading = false;
            } else {
                s.alpha += delta * 0.5f;
                if (s.alpha >= 0.9f) s.fading = true;
            }

            if (s.y < 0 || s.x < 0) {
                snowflakesList.set(i, createSnowflake(false));
            }
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
