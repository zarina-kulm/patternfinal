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

public class GameOverScreen implements Screen {

    private final WarOfRealms game;
    private BitmapFont font;
    private ShapeRenderer sr;
    private AnimatedBackground bg;
    private float time = 0;

    public GameOverScreen(WarOfRealms game) {
        this.game = game;
        this.font = new BitmapFont();
        this.sr = new ShapeRenderer();
        this.bg = new AnimatedBackground(80);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        time += delta;
        bg.update(delta); bg.render(sr);

        float g=(float)(Math.sin(time*1.5f)*0.08+0.15);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(g*3,0f,0f,0.85f);
        sr.rect(340,300,600,300);
        sr.end();
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.RED);
        sr.rect(340,300,600,300);
        sr.end();

        GameStateSingleton s=GameStateSingleton.getInstance();
        game.batch.begin();
        font.getData().setScale(2.8f);
        font.setColor(Color.RED);
        font.draw(game.batch,"GAME OVER",420,568);
        font.getData().setScale(1.1f);
        font.setColor(new Color(0.8f,0.6f,0.6f,1f));
        font.draw(game.batch,"Your house has fallen...",470,506);
        font.setColor(Color.WHITE);
        font.draw(game.batch,"Final Score: "+s.getScore(),490,472);
        font.draw(game.batch,"Wave reached: "+s.getWave(),490,444);
        font.getData().setScale(1f);
        font.setColor(Color.YELLOW);
        font.draw(game.batch,"ENTER - retry  |  ESC - menu",455,355);
        game.batch.end();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            GameStateSingleton.getInstance().reset();
            game.setScreen(new GameScreen(game));
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            GameStateSingleton.getInstance().reset();
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { font.dispose(); sr.dispose(); }
}
