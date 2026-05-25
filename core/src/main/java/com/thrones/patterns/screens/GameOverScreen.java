package com.thrones.patterns.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.thrones.patterns.WarOfRealms;
import com.thrones.patterns.patterns.singleton.GameStateSingleton;

public class GameOverScreen implements Screen {

    private final WarOfRealms game;
    private BitmapFont font;
    private ShapeRenderer sr;
    private Texture bgTexture;
    private float time = 0f;

    private int bestScore, bestLevel;
    private int currentScore, currentLevel;
    private boolean isNewRecord;
    private String selectedQuote;

    private static final String[] QUOTES = {
        "You never sat on the Iron Throne...",
        "The realm has no mercy for the weak.",
        "Winter came. And so did your end.",
        "The throne remains cold and empty.",
        "Another house falls into darkness.",
        "Not all warriors become kings.",
        "The White Walkers do not forgive.",
        "Your bloodline ends here.",
        "The North remembers your failure.",
        "Power was never yours to claim."
    };

    public GameOverScreen(WarOfRealms game) {
        this.game = game;
        this.font = new BitmapFont();
        this.sr = new ShapeRenderer();

        // Обязательно проверь, чтобы имя файла совпадало с твоей картинкой в папке assets!
        bgTexture = new Texture(Gdx.files.internal("background3.jpeg"));

        GameStateSingleton state = GameStateSingleton.getInstance();
        currentScore = state.getScore();
        currentLevel = state.getLevel();
        loadAndSaveRecords();
        selectedQuote = QUOTES[(int)(Math.random() * QUOTES.length)];
    }

    private void loadAndSaveRecords() {
        try {
            Preferences p = Gdx.app.getPreferences("ThronesRecords");
            bestScore = p.getInteger("bestScore", 0);
            bestLevel = p.getInteger("bestLevel", 0);
            isNewRecord = false;
            if (currentScore > bestScore) {
                bestScore = currentScore;
                p.putInteger("bestScore", bestScore);
                isNewRecord = true;
            }
            if (currentLevel > bestLevel) {
                bestLevel = currentLevel;
                p.putInteger("bestLevel", bestLevel);
                isNewRecord = true;
            }
            p.flush();
        } catch (Exception e) {
            bestScore = 0;
            bestLevel = 0;
            isNewRecord = false;
        }
    }

    @Override
    public void render(float delta) {
        // Очищаем экран стандартным черным цветом перед каждым кадром
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        time += delta;

        // ── 1. ОТРИСОВКА КАРТИНКИ ФОНА ──
        game.batch.begin();
        if (bgTexture != null) {
            game.batch.setColor(1f, 1f, 1f, 1f);
            game.batch.draw(bgTexture, 0, 0, 1280, 720);
        } else {
            // Если текстура вдруг не загрузилась, зальем красивым темно-синим цветом
            Gdx.gl.glClearColor(0.05f, 0.06f, 0.12f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
        game.batch.end();

        // ── 2. ЭФФЕКТ ЗАТЕМНЕНИЯ (OVERLAY) ──
        // Включаем OpenGL блендинг, чтобы прозрачность (0.35f) заработала и не перекрывала фон наглухо
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.35f);
        sr.rect(0, 0, 1280, 720);
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND); // Выключаем после использования

        // Рисуем панели результатов и обрабатываем ввод
        drawPanel();
        handleInput();
    }

    private void drawPanel() {
        // Включаем блендинг для полупрозрачной центральной панели
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // ── ПАНЕЛЬ — мөлдір қараңғы ──
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.6f);
        sr.rect(240, 75, 800, 570);
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND); // Отключаем блендинг для сплошных линий жиека

        // ── ПАНЕЛЬ ЖИЕГІ — алтын түс ──
        sr.begin(ShapeRenderer.ShapeType.Line);
        float pulse = (float)(Math.sin(time * 1.5f) * 0.3f + 0.7f);
        sr.setColor(0.8f * pulse, 0.6f * pulse, 0.1f * pulse, 1f);
        sr.rect(240, 75, 800, 570);
        sr.rect(244, 79, 792, 562);
        sr.end();

        // ── Декоративті сызықтар ──
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.7f, 0.5f, 0.1f, 0.7f);
        sr.rect(280, 618, 720, 2);
        sr.rect(280, 82, 720, 2);
        sr.end();

        game.batch.begin();

        // ── GAME OVER тақырып — алтын ──
        font.getData().setScale(3.2f);
        float tp = (float)(Math.sin(time * 1.8f) * 0.15f + 0.85f);
        font.setColor(tp, tp * 0.75f, 0f, 1f);
        font.draw(game.batch, "GAME  OVER", 355, 610);

        // ── Атмосфералық сөз ──
        font.getData().setScale(0.95f);
        font.setColor(new Color(0.85f, 0.75f, 0.45f, 0.9f));
        font.draw(game.batch, "\"" + selectedQuote + "\"", 280, 558);

        game.batch.end();
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.6f, 0.45f, 0.1f, 0.6f);
        sr.rect(280, 534, 720, 1);
        sr.end();
        game.batch.begin();

        // ── YOUR RESULTS ──
        font.getData().setScale(1.15f);
        font.setColor(new Color(1f, 0.92f, 0.6f, 1f));
        font.draw(game.batch, "YOUR RESULTS", 510, 518);

        font.getData().setScale(1f);
        font.setColor(new Color(0.85f, 0.8f, 0.7f, 1f));
        font.draw(game.batch, "Level reached:", 310, 487);
        font.setColor(new Color(1f, 0.9f, 0.3f, 1f));
        font.draw(game.batch, currentLevel + " / 7", 780, 487);

        font.setColor(new Color(0.85f, 0.8f, 0.7f, 1f));
        font.draw(game.batch, "Final score:", 310, 458);
        font.setColor(new Color(1f, 0.9f, 0.3f, 1f));
        font.draw(game.batch, String.valueOf(currentScore), 780, 458);

        game.batch.end();
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.5f, 0.38f, 0.08f, 0.5f);
        sr.rect(280, 434, 720, 1);
        sr.end();
        game.batch.begin();

        // ── HALL OF RECORDS ──
        font.getData().setScale(1.15f);
        font.setColor(new Color(1f, 0.85f, 0.2f, 1f));
        font.draw(game.batch, "HALL  OF  RECORDS", 470, 418);

        if (isNewRecord) {
            float rp = (float)(Math.sin(time * 5f) * 0.4f + 0.6f);
            font.getData().setScale(1f);
            font.setColor(rp, rp * 0.85f, 0.1f, 1f);
            font.draw(game.batch, "★  NEW RECORD!  ★", 470, 390);
        }

        float ry = isNewRecord ? 360 : 383;

        font.getData().setScale(1f);
        font.setColor(new Color(0.85f, 0.8f, 0.7f, 1f));
        font.draw(game.batch, "Best level:", 310, ry);
        font.setColor(new Color(1f, 0.85f, 0.2f, 1f));
        font.draw(game.batch, bestLevel + " / 9", 780, ry);

        font.setColor(new Color(0.85f, 0.8f, 0.7f, 1f));
        font.draw(game.batch, "Best score:", 310, ry - 28);
        font.setColor(new Color(1f, 0.85f, 0.2f, 1f));
        font.draw(game.batch, String.valueOf(bestScore), 780, ry - 28);

        game.batch.end();

        // Включаем блендинг для полупрозрачного фона кнопки под текстом
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // ── Батырма фоны ──
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.5f, 0.38f, 0.08f, 0.5f);
        sr.rect(280, ry - 56, 720, 1);

        float bp = (float)(Math.sin(time * 2f) * 0.08f + 0.12f);
        sr.setColor(bp * 4, bp * 3, 0f, 0.7f);
        sr.rect(290, ry - 108, 265, 38);
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(0.8f, 0.6f, 0.1f, 0.9f);
        sr.rect(290, ry - 108, 265, 38);
        sr.end();

        game.batch.begin();
        float btnY = ry - 86;
        font.getData().setScale(1.05f);
        font.setColor(new Color(1f, 0.9f, 0.3f, 1f));
        font.draw(game.batch, "[ ENTER ]  Try Again", 305, btnY);
        font.setColor(new Color(0.7f, 0.65f, 0.55f, 0.9f));
        font.draw(game.batch, "[ ESC ]  Main Menu", 720, btnY);

        font.getData().setScale(0.8f);
        font.setColor(new Color(0.65f, 0.55f, 0.35f, 0.75f));
        font.draw(game.batch, "The Iron Throne awaits those who dare again...", 355, 108);

        font.getData().setScale(1f);
        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            GameStateSingleton.getInstance().reset();
            game.setScreen(new GameScreen(game));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            GameStateSingleton.getInstance().reset();
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (font != null) font.dispose();
        if (sr != null) sr.dispose();
        if (bgTexture != null) bgTexture.dispose();
    }
}
