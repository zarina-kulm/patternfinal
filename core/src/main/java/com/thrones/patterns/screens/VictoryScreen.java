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
import com.thrones.patterns.utils.AnimatedBackground;

public class VictoryScreen implements Screen {

    private final WarOfRealms game;

    private BitmapFont font;
    private ShapeRenderer sr;
    private AnimatedBackground bg;

    private Texture background;
    private Music victoryMusic;

    private float time = 0f;

    public VictoryScreen(WarOfRealms game) {
        this.game = game;
    }

    @Override
    public void show() {
        font = new BitmapFont();
        sr = new ShapeRenderer();
        bg = new AnimatedBackground(220);

        for (int i = 0; i < 45; i++) {
            bg.spawnParticle(
                (float) (Math.random() * 1280),
                (float) (Math.random() * 720),
                Color.GOLD
            );
        }

        try {
            background = new Texture("ui/backgrounds/throne_room.png");
        } catch (Exception e) {
            background = null;
        }

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

        Gdx.gl.glClearColor(0.01f, 0.005f, 0.01f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBackground(delta);
        drawThroneRoom();
        drawPanels();
        drawText();

        handleInput();
    }

    private void drawBackground(float delta) {
        game.batch.begin();

        if (background != null) {
            game.batch.setColor(0.6f, 0.55f, 0.5f, 1f);
            game.batch.draw(background, 0, 0, 1280, 720);
            game.batch.setColor(Color.WHITE);
        }

        game.batch.end();

        bg.update(delta);
        bg.render(sr);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.42f);
        sr.rect(0, 0, 1280, 720);
        sr.end();
    }

    private void drawThroneRoom() {
        float glow = (float) (Math.sin(time * 2f) * 0.1f + 0.35f);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        // floor
        sr.setColor(0.08f, 0.045f, 0.025f, 1f);
        sr.rect(0, 0, 1280, 170);

        // red carpet
        sr.setColor(0.28f, 0.02f, 0.02f, 0.9f);
        sr.triangle(520, 0, 760, 0, 655, 310);

        // throne shadow
        sr.setColor(0f, 0f, 0f, 0.7f);
        sr.rect(530, 245, 220, 280);

        // throne body
        sr.setColor(0.22f, 0.16f, 0.08f, 1f);
        sr.rect(560, 260, 160, 230);

        // throne back spikes
        sr.setColor(0.34f, 0.25f, 0.10f, 1f);
        sr.triangle(560, 490, 590, 490, 575, 560);
        sr.triangle(605, 490, 635, 490, 620, 590);
        sr.triangle(650, 490, 680, 490, 665, 590);
        sr.triangle(695, 490, 725, 490, 710, 560);

        // seat
        sr.setColor(0.42f, 0.30f, 0.12f, 1f);
        sr.rect(540, 250, 200, 45);

        // golden glow behind throne
        sr.setColor(0.95f, 0.65f, 0.08f, glow);
        sr.circle(640, 430, 150, 40);

        // torches
        drawTorch(285, 350);
        drawTorch(980, 350);

        sr.end();
    }

    private void drawTorch(float x, float y) {
        float flame = (float) (Math.sin(time * 8f + x) * 0.25f + 0.75f);

        sr.setColor(0.20f, 0.12f, 0.06f, 1f);
        sr.rect(x, y - 80, 12, 80);

        sr.setColor(1f, 0.32f, 0.02f, 0.9f);
        sr.circle(x + 6, y, 22 * flame, 16);

        sr.setColor(1f, 0.82f, 0.05f, 0.75f);
        sr.circle(x + 6, y + 5, 12 * flame, 12);
    }

    private void drawPanels() {
        float panelGlow = (float) (Math.sin(time * 2f) * 0.08f + 0.28f);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        sr.setColor(0f, 0f, 0f, 0.76f);
        sr.rect(285, 430, 710, 185);

        sr.setColor(0f, 0f, 0f, 0.72f);
        sr.rect(330, 135, 620, 205);

        sr.setColor(0.65f, 0.42f, 0.05f, panelGlow);
        sr.rect(285, 430, 710, 185);

        sr.end();

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.GOLD);
        sr.rect(285, 430, 710, 185);
        sr.rect(330, 135, 620, 205);
        sr.end();
    }

    private void drawText() {
        GameStateSingleton state = GameStateSingleton.getInstance();

        game.batch.begin();

        font.getData().setScale(2.8f);
        font.setColor(Color.GOLD);
        font.draw(game.batch, "THE THRONE IS YOURS", 330, 585);

        font.getData().setScale(1.05f);
        font.setColor(new Color(0.86f, 0.78f, 0.62f, 1f));
        font.draw(game.batch, "After nine brutal battles, the Last Heir reclaimed the cursed throne.", 335, 535);
        font.draw(game.batch, "But every crown is built on blood, betrayal and sacrifice.", 390, 505);

        font.getData().setScale(1.25f);
        font.setColor(Color.GOLD);
        font.draw(game.batch, "FINAL CAMPAIGN RESULT", 500, 315);

        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
        font.draw(game.batch, "Levels completed: 9 / 9", 470, 275);
        font.draw(game.batch, "Final score: " + state.getScore(), 470, 245);
        font.draw(game.batch, "Gold collected: " + state.getGold(), 470, 215);
        font.draw(game.batch, "Title earned: King of the War Realms", 470, 185);

        font.getData().setScale(0.9f);
        font.setColor(Color.YELLOW);
        font.draw(game.batch, "ENTER - return to main menu  |  ESC - exit", 430, 85);

        font.getData().setScale(0.82f);
        font.setColor(new Color(0.55f, 0.35f, 0.25f, 1f));
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
        if (victoryMusic != null) victoryMusic.dispose();
    }
}
