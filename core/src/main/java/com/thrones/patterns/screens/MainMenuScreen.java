package com.thrones.patterns.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.thrones.patterns.WarOfRealms;
import com.thrones.patterns.patterns.singleton.GameStateSingleton;
import com.thrones.patterns.utils.AnimatedBackground;
import com.thrones.patterns.utils.CharacterRenderer;

public class MainMenuScreen implements Screen {

    private final WarOfRealms game;
    private BitmapFont font;
    private ShapeRenderer sr;
    private AnimatedBackground bg;
    private int selected = 0;
    private final String[] options = {"PLAY GAME", "SELECT HERO", "QUIT"};
    private float time = 0;

    public MainMenuScreen(WarOfRealms game) {
        this.game = game;
        this.font = new BitmapFont();
        this.sr = new ShapeRenderer();
        this.bg = new AnimatedBackground(150);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        time += delta;
        handleInput();
        bg.update(delta);
        bg.render(sr);

        CharacterRenderer.drawKnight(sr, 80, 200, 1.2f, time, false);
        CharacterRenderer.drawDragon(sr, 980, 180, 0.7f, time, false);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.6f);
        sr.rect(300, 490, 680, 120);
        sr.end();
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.GOLD);
        sr.rect(300, 490, 680, 120);
        sr.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.5f);
        sr.rect(460, 280, 360, 195);

        float glow = (float)(Math.sin(time*3f)*0.15+0.35);
        sr.setColor(0.7f, 0.5f, 0f, glow);
        sr.rect(462, 358 - selected*50, 356, 38);
        sr.end();

        game.batch.begin();
        font.getData().setScale(2.2f);
        font.setColor(Color.GOLD);
        font.draw(game.batch, "THRONES OF PATTERNS", 310, 590);
        font.getData().setScale(1.4f);
        font.setColor(new Color(0.8f,0.7f,0.9f,1f));
        font.draw(game.batch, "War of Realms", 500, 545);
        font.getData().setScale(1f);
        font.setColor(new Color(0.5f,0.4f,0.6f,1f));
        font.draw(game.batch, "A dark 2D strategy game", 480, 510);

        for (int i = 0; i < options.length; i++) {
            font.getData().setScale(1.3f);
            font.setColor(i == selected ? Color.YELLOW : Color.WHITE);
            String txt = i == selected ? "> "+options[i]+" <" : options[i];
            font.draw(game.batch, txt, i==selected ? 495:520, 388-i*50);
        }

        font.getData().setScale(0.9f);
        font.setColor(Color.DARK_GRAY);
        font.draw(game.batch, "UP/DOWN arrows  |  ENTER to select", 430, 60);
        font.getData().setScale(1f);
        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
            selected = (selected+1) % options.length;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
            selected = (selected-1+options.length) % options.length;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (selected) {
                case 0:
                    GameStateSingleton.getInstance().reset();
                    game.setScreen(new GameScreen(game)); break;
                case 1:
                    game.setScreen(new HeroSelectScreen(game)); break;
                case 2:
                    Gdx.app.exit(); break;
            }
        }
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { font.dispose(); sr.dispose(); }
}
