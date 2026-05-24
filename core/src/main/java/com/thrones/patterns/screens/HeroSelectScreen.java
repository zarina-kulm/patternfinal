package com.thrones.patterns.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.thrones.patterns.WarOfRealms;
import com.thrones.patterns.patterns.factory.*;
import com.thrones.patterns.patterns.singleton.GameStateSingleton;
import com.thrones.patterns.utils.AnimatedBackground;
import com.thrones.patterns.utils.CharacterRenderer;

public class HeroSelectScreen implements Screen {

    private final WarOfRealms game;
    private BitmapFont font;
    private ShapeRenderer sr;
    private AnimatedBackground bg;

    private final HouseFactory[] houses = {
        new IronforgeHouseFactory(),
        new ShadowmereHouseFactory(),
        new SwiftwindHouseFactory()
    };
    private final String[] heroTypes = {"KNIGHT", "MAGE", "ARCHER"};
    private final String[][] stats = {
        {"HP: 200", "ATK: 30", "DEF: 20", "Shield Bash"},
        {"HP: 120", "ATK: 60", "DEF: 5",  "Arcane Nova"},
        {"HP: 140", "ATK: 45", "DEF: 8",  "Rain of Arrows"}
    };
    private int selected = 0;
    private float time = 0;

    public HeroSelectScreen(WarOfRealms game) {
        this.game = game;
        this.font = new BitmapFont();
        this.sr = new ShapeRenderer();
        this.bg = new AnimatedBackground(100);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        time += delta;
        handleInput();
        bg.update(delta);
        bg.render(sr);

        for (int i = 0; i < 3; i++) {
            float cx = 200 + i*300;
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(i==selected ? 0.2f:0.08f, i==selected?0.15f:0.06f,
                i==selected?0.3f:0.12f, 0.9f);
            sr.rect(cx-80, 160, 200, 340);
            sr.end();
            sr.begin(ShapeRenderer.ShapeType.Line);
            if (i == selected) {
                float g = (float)(Math.sin(time*3f)*0.3+0.7);
                sr.setColor(g, g*0.8f, 0f, 1f);
            } else {
                sr.setColor(0.3f, 0.2f, 0.4f, 1f);
            }
            sr.rect(cx-80, 160, 200, 340);
            sr.end();
        }

        CharacterRenderer.drawKnight(sr, 150, 200, 1.0f, time, false);
        CharacterRenderer.drawMage(sr, 430, 200, 1.0f, time, false);
        CharacterRenderer.drawArcher(sr, 720, 200, 1.0f, time, false);

        game.batch.begin();
        font.getData().setScale(1.8f);
        font.setColor(Color.GOLD);
        font.draw(game.batch, "CHOOSE YOUR HERO", 420, 680);

        for (int i = 0; i < 3; i++) {
            float cx = 200 + i*300;
            font.getData().setScale(1f);
            font.setColor(i==selected ? Color.YELLOW : Color.WHITE);
            font.draw(game.batch, houses[i].getHouseName(), cx-70, 490);
            font.getData().setScale(1.2f);
            font.setColor(i==selected ? Color.CYAN : Color.LIGHT_GRAY);
            font.draw(game.batch, heroTypes[i], cx-25, 460);
            font.getData().setScale(0.85f);
            font.setColor(Color.GRAY);
            for (int j = 0; j < stats[i].length; j++) {
                font.draw(game.batch, stats[i][j], cx-70, 430-j*22);
            }
        }

        font.getData().setScale(0.9f);
        font.setColor(new Color(0.7f,0.6f,0.9f,1f));
        font.draw(game.batch, houses[selected].getHouseDescription(), 200, 120);
        font.setColor(Color.GRAY);
        font.draw(game.batch,
            "LEFT/RIGHT - choose  |  ENTER - confirm  |  ESC - back", 310, 60);
        font.getData().setScale(1f);
        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) selected=(selected+1)%3;
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) selected=(selected-1+3)%3;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            GameStateSingleton.getInstance().setSelectedHeroType(heroTypes[selected]);
            game.setScreen(new GameScreen(game));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            game.setScreen(new MainMenuScreen(game));
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { font.dispose(); sr.dispose(); }
}
