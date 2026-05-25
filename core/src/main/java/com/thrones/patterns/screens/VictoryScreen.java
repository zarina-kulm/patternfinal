package com.thrones.patterns.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.thrones.patterns.WarOfRealms;
import com.thrones.patterns.patterns.singleton.GameStateSingleton;

public class VictoryScreen implements Screen {

    private final WarOfRealms game;

    private BitmapFont font;
    private ShapeRenderer sr;

    private Texture background;
    private Texture kingPortrait;
    private Music victoryMusic;

    private float time = 0f;

    public VictoryScreen(WarOfRealms game) {
        this.game = game;
    }

    @Override
    public void show() {
        font = new BitmapFont();
        sr = new ShapeRenderer();

        // ── background3.jpeg ФОНДЫҚ СУРЕТІН ЖҮКТЕУ ──
        try {
            background = new Texture("background3.jpeg");
        } catch (Exception e) {
            try {
                // Егер assets түбінен таппаса, ui/backgrounds/ ішінен іздейді
                background = new Texture("ui/backgrounds/background3.jpeg");
            } catch (Exception ex) {
                background = null;
                Gdx.app.log("VictoryScreen", "Error: background3.jpeg not found!");
            }
        }

        // Портретті жүктеу
        try {
            kingPortrait = new Texture(Gdx.files.internal("jon_snow_portrait.png"));
        } catch (Exception e) {
            kingPortrait = null;
        }

        // Музыканы жүктеу және қосу
        try {
            victoryMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/victory_theme.mp3"));
            victoryMusic.setLooping(true);
            victoryMusic.setVolume(0.35f);
            victoryMusic.play();
        } catch (Exception e) {
            victoryMusic = null;
        }
    }

    @Override
    public void render(float delta) {
        time += delta;


        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        game.batch.begin();
        if (background != null) {
            game.batch.setColor(Color.WHITE);
            game.batch.draw(background, 0, 0, 1280, 720); // Толық экран өлшемі
        }
        game.batch.end();


        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.35f); // 35% күңгірт қара фильтр
        sr.rect(0, 0, 1280, 720);
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);


        drawPanels();
        drawTextAndPortrait();

        handleInput();
    }

    private void drawPanels() {
        float panelGlow = (float) (Math.sin(time * 2f) * 0.08f + 0.28f);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.75f);
        sr.rect(285, 430, 710, 185); // Жоғарғы панель
        sr.rect(285, 135, 710, 225); // Төменгі панель
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);


        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.GOLD);
        sr.rect(285, 430, 710, 185);
        sr.rect(285, 135, 710, 225);
        sr.end();
    }

    private void drawTextAndPortrait() {
        GameStateSingleton state = GameStateSingleton.getInstance();

        game.batch.begin();


        font.getData().setScale(2.8f);
        font.setColor(Color.GOLD);
        font.draw(game.batch, "THE THRONE IS YOURS", 330, 585);

        font.getData().setScale(1.05f);
        font.setColor(new Color(0.86f, 0.78f, 0.62f, 1f));
        font.draw(game.batch, "After seven brutal battles, the Last Heir reclaimed the cursed throne.", 335, 535);
        font.draw(game.batch, "But every crown is built on blood, betrayal and sacrifice.", 390, 505);


        if (kingPortrait != null) {
            game.batch.setColor(1f, 1f, 1f, 1f);
            game.batch.draw(kingPortrait, 320, 175, 140, 140);
        }

        font.getData().setScale(1.25f);
        font.setColor(Color.GOLD);
        font.draw(game.batch, "FINAL CAMPAIGN RESULT", 520, 325);

        font.getData().setScale(1.1f);
        font.setColor(Color.WHITE);
        font.draw(game.batch, "Levels completed: 7 / 7", 520, 285);
        font.draw(game.batch, "Final score: " + state.getScore(), 520, 255);
        font.draw(game.batch, "Gold collected: " + state.getGold(), 520, 225);

        font.setColor(Color.GOLD);
        font.draw(game.batch, "Title earned: King of the War Realms", 520, 195);

        // ── БАСҚАРУ БАТЫРМАЛАРЫ ──
        font.getData().setScale(0.9f);
        font.setColor(Color.YELLOW);
        font.draw(game.batch, "ENTER - return to main menu  |  ESC - exit", 430, 85);

        font.getData().setScale(0.82f);
        font.setColor(new Color(0.75f, 0.70f, 0.65f, 1f));
        font.draw(game.batch, "The war is over. The realm remembers your name.", 435, 55);

        font.getData().setScale(1f);
        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            GameStateSingleton.getInstance().reset();
            stopMusic();
            game.setScreen(new MainMenuScreen(game));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void stopMusic() {
        if (victoryMusic != null) {
            victoryMusic.stop();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        stopMusic();
    }

    @Override
    public void dispose() {
        if (font != null) font.dispose();
        if (sr != null) sr.dispose();
        if (background != null) background.dispose();
        if (kingPortrait != null) kingPortrait.dispose();
        if (victoryMusic != null) victoryMusic.dispose();
    }
}
