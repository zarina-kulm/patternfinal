package com.thrones.patterns.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.thrones.patterns.WarOfRealms;
import com.thrones.patterns.patterns.singleton.GameStateSingleton;

public class MainMenuScreen implements Screen {

    private final WarOfRealms game;
    private Texture backgroundTexture;

    // 1280x720 экранға нақты пиксель позициялары
    private static final float START_X = 490f, START_Y = 218f;
    private static final float MENU_X  = 490f, MENU_Y  = 138f;
    private static final float EXIT_X  = 490f, EXIT_Y  = 55f;
    private static final float BTN_W   = 300f, BTN_H   = 58f;

    public MainMenuScreen(WarOfRealms game) {
        this.game = game;
        this.backgroundTexture = new Texture(
            Gdx.files.internal("background.jpeg"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Фон суреті
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, 1280, 720);
        game.batch.end();

        handleInput();
        handleMouseClick();
    }

    private void handleMouseClick() {
        if (!Gdx.input.justTouched()) return;

        float mx = Gdx.input.getX();
        float my = 720 - Gdx.input.getY();

        if (isHit(mx, my, START_X, START_Y)) activate(0);
        else if (isHit(mx, my, MENU_X, MENU_Y)) activate(1);
        else if (isHit(mx, my, EXIT_X, EXIT_Y)) activate(2);
    }

    private boolean isHit(float mx, float my, float bx, float by) {
        return mx >= bx && mx <= bx + BTN_W
            && my >= by && my <= by + BTN_H;
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            activate(0); // ENTER — бірден START
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void activate(int index) {
        switch (index) {
            case 0:
                GameStateSingleton.getInstance().reset();
                game.setScreen(new HeroSelectScreen(game));
                break;
            case 1:
                game.setScreen(new HeroSelectScreen(game));
                break;
            case 2:
                Gdx.app.exit();
                break;
        }
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        backgroundTexture.dispose();
    }
}
