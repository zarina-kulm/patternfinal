package com.thrones.patterns.screens;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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

        loadBackground();
        loadPortrait();
        loadVictoryMusic();
    }

    private void loadBackground() {
        try {
            background = new Texture(Gdx.files.internal("background3.jpeg"));
        } catch (Exception e) {
            try {
                background = new Texture(Gdx.files.internal("ui/backgrounds/background3.jpeg"));
            } catch (Exception ex) {
                background = null;
                System.out.println("VICTORY BACKGROUND NOT FOUND");
            }
        }
    }

    private void loadPortrait() {
        try {
            kingPortrait = new Texture(Gdx.files.internal("jon_snow_portrait.png"));
        } catch (Exception e) {
            kingPortrait = null;
            System.out.println("KING PORTRAIT NOT FOUND");
        }
    }

    private void loadVictoryMusic() {
        try {
            System.out.println("Trying to load victory music...");

            victoryMusic = Gdx.audio.newMusic(
                Gdx.files.internal("sounds/victory_theme.mp3")
            );

            victoryMusic.setLooping(false);
            victoryMusic.setVolume(1.0f);
            victoryMusic.play();

            System.out.println("VICTORY MUSIC PLAYING");

        } catch (Exception e) {
            victoryMusic = null;
            System.out.println("VICTORY MUSIC NOT FOUND OR CANNOT PLAY");
            e.printStackTrace();
        }
    }

    @Override
    public void render(float delta) {
        time += delta;

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBackground();
        drawDarkOverlay();
        drawPanels();
        drawTextAndPortrait();

        handleInput();
    }

    private void drawBackground() {
        game.batch.begin();

        if (background != null) {
            game.batch.setColor(Color.WHITE);
            game.batch.draw(background, 0, 0, 1280, 720);
        }

        game.batch.end();
    }

    private void drawDarkOverlay() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.35f);
        sr.rect(0, 0, 1280, 720);
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawPanels() {
        float panelGlow =
            (float) (Math.sin(time * 2f) * 0.08f + 0.28f);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.75f);
        sr.rect(285, 430, 710, 185);
        sr.rect(285, 135, 710, 225);
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(1f, 0.75f + panelGlow, 0f, 1f);
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
        font.draw(game.batch,
            "After seven brutal battles, the Last Heir reclaimed the cursed throne.",
            335,
            535
        );
        font.draw(game.batch,
            "But every crown is built on blood, betrayal and sacrifice.",
            390,
            505
        );

        if (kingPortrait != null) {
            game.batch.setColor(Color.WHITE);
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

        font.getData().setScale(0.9f);
        font.setColor(Color.YELLOW);
        font.draw(game.batch,
            "ENTER - return to main menu  |  ESC - exit",
            430,
            85
        );

        font.getData().setScale(0.82f);
        font.setColor(new Color(0.75f, 0.70f, 0.65f, 1f));
        font.draw(game.batch,
            "The war is over. The realm remembers your name.",
            435,
            55
        );

        font.getData().setScale(1f);
        game.batch.setColor(Color.WHITE);
        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            GameStateSingleton.getInstance().reset();
            stopMusic();
            game.setScreen(new MainMenuScreen(game));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            stopMusic();
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

        if (victoryMusic != null) {
            victoryMusic.dispose();
            victoryMusic = null;
        }
    }
}
