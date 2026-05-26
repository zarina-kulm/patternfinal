package com.thrones.patterns.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.thrones.patterns.WarOfRealms;
import com.thrones.patterns.patterns.factory.*;
import com.thrones.patterns.patterns.singleton.GameStateSingleton;
import com.thrones.patterns.utils.AnimatedBackground;

public class HeroSelectScreen implements Screen {

    private final WarOfRealms game;
    private BitmapFont font;
    private ShapeRenderer sr;
    private AnimatedBackground bg;
    private Texture knightTex, mageTex, archerTex;

    private final HouseFactory[] houses = {
        new IronforgeHouseFactory(),
        new ShadowmereHouseFactory(),
        new SwiftwindHouseFactory()
    };

    private final String[] heroTypes = {"KNIGHT", "MAGE", "ARCHER"};
    private final String[] heroNames = {"DAENERYS", "CERSEI", "SANSA"};

    private final String[][] stats = {
        {"HP: 200", "ATK: 30", "DEF: 20", "Shield Bash"},
        {"HP: 120", "ATK: 60", "DEF: 5",  "Arcane Nova"},
        {"HP: 140", "ATK: 45", "DEF: 8",  "Rain of Arrows"}
    };

    private int selected = 0;
    private float time = 0;

    // Карта өлшемдері
    private static final float CARD_W = 240f;
    private static final float CARD_H = 360f;
    private static final float CARD_Y = 140f;
    private static final float[] CARD_X = {80f, 390f, 700f};

    public HeroSelectScreen(WarOfRealms game) {
        this.game = game;
        this.font = new BitmapFont();
        this.sr = new ShapeRenderer();
        this.bg = new AnimatedBackground(100);
        knightTex = new Texture(Gdx.files.internal("knight.png"));
        mageTex   = new Texture(Gdx.files.internal("mage.png"));
        archerTex = new Texture(Gdx.files.internal("archer.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        time += delta;
        handleInput();
        bg.update(delta);
        bg.render(sr);
        drawCards();
        drawCharacters();
        drawText();
    }

    private void drawCards() {
        for (int i = 0; i < 3; i++) {
            // Карта фоны
            sr.begin(ShapeRenderer.ShapeType.Filled);
            if (i == selected) {
                sr.setColor(0.18f, 0.12f, 0.28f, 0.92f);
            } else {
                sr.setColor(0.06f, 0.04f, 0.1f, 0.88f);
            }
            sr.rect(CARD_X[i], CARD_Y, CARD_W, CARD_H);
            sr.end();

            // Жиек
            sr.begin(ShapeRenderer.ShapeType.Line);
            if (i == selected) {
                float g = (float)(Math.sin(time * 3f) * 0.3f + 0.7f);
                sr.setColor(g, g * 0.8f, 0f, 1f);
            } else {
                sr.setColor(0.3f, 0.2f, 0.4f, 1f);
            }
            sr.rect(CARD_X[i], CARD_Y, CARD_W, CARD_H);
            sr.end();
        }
    }

    private void drawCharacters() {
        game.batch.begin();
        // Кейіпкерлер карта ортасына, жоғарырақ
        float charW = 160f, charH = 260f;
        game.batch.draw(knightTex, CARD_X[0] + 40, CARD_Y + 80, charW, charH);
        game.batch.draw(mageTex,   CARD_X[1] + 40, CARD_Y + 80, charW, charH);
        game.batch.draw(archerTex, CARD_X[2] + 40, CARD_Y + 80, charW, charH);
        game.batch.end();
    }

    private void drawText() {
        game.batch.begin();

        // Тақырып
        font.getData().setScale(1.8f);
        font.setColor(Color.GOLD);
        font.draw(game.batch, "CHOOSE YOUR HERO", 390, 685);

        for (int i = 0; i < 3; i++) {
            float cx = CARD_X[i];

            // Үй аты — карта үстінде
            font.getData().setScale(0.9f);
            font.setColor(i == selected ? Color.YELLOW : Color.LIGHT_GRAY);
            font.draw(game.batch, houses[i].getHouseName(), cx + 10, CARD_Y + CARD_H + 30);

            // Герой аты — карта ішінде жоғарыда
            font.getData().setScale(1.1f);
            font.setColor(i == selected ? Color.CYAN : Color.WHITE);
            font.draw(game.batch, heroNames[i], cx + 30, CARD_Y + CARD_H - 15);

            // Статистика — карта астында
            font.getData().setScale(0.78f);
            font.setColor(new Color(0.85f, 0.75f, 0.5f, 1f));
            for (int j = 0; j < stats[i].length; j++) {
                font.draw(game.batch, stats[i][j], cx + 8, CARD_Y + 72 - j * 18);
            }
        }

        // Сипаттама
        font.getData().setScale(0.85f);
        font.setColor(new Color(0.7f, 0.6f, 0.9f, 1f));
        font.draw(game.batch, houses[selected].getHouseDescription(), 150, 100);

        // Нұсқау
        font.getData().setScale(0.8f);
        font.setColor(Color.DARK_GRAY);
        font.draw(game.batch,
            "LEFT/RIGHT - choose  |  ENTER - confirm  |  ESC - back", 310, 55);

        font.getData().setScale(1f);
        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
            selected = (selected + 1) % 3;
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
            selected = (selected - 1 + 3) % 3;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
            confirmHero();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            game.setScreen(new MainMenuScreen(game));
    }

    private void confirmHero() {
        GameStateSingleton state = GameStateSingleton.getInstance();
        state.reset();
        state.setSelectedHeroType(heroTypes[selected]);
        if (MainMenuScreen.menuMusic != null) {
            MainMenuScreen.menuMusic.stop();
        }

        game.setScreen(new GameScreen(game));
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        font.dispose();
        sr.dispose();
        knightTex.dispose();
        mageTex.dispose();
        archerTex.dispose();
    }
}
