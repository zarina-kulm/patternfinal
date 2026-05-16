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

public class VictoryScreen implements Screen {

    private final WarOfRealms game;
    private BitmapFont font;
    private ShapeRenderer sr;
    private AnimatedBackground bg;
    private float time = 0;

    public VictoryScreen(WarOfRealms game) {
        this.game = game;
        this.font = new BitmapFont();
        this.sr = new ShapeRenderer();
        this.bg = new AnimatedBackground(200);
        for(int i=0;i<30;i++)
            bg.spawnParticle((float)(Math.random()*1280),
                (float)(Math.random()*720), Color.GOLD);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        time += delta;
        bg.update(delta); bg.render(sr);

        float g=(float)(Math.sin(time*2f)*0.1+0.2);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(g,g*0.8f,0f,0.8f);
        sr.rect(340,320,600,280);
        sr.end();
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.GOLD);
        sr.rect(340,320,600,280);
        sr.end();

        GameStateSingleton s=GameStateSingleton.getInstance();
        game.batch.begin();
        font.getData().setScale(3f);
        font.setColor(Color.GOLD);
        font.draw(game.batch,"VICTORY!",440,570);
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        font.draw(game.batch,"Wave "+(s.getWave()-1)+" cleared!",510,510);
        font.draw(game.batch,"Score: "+s.getScore(),530,475);
        font.draw(game.batch,"Gold:  "+s.getGold(),530,447);
        font.getData().setScale(1f);
        font.setColor(Color.YELLOW);
        font.draw(game.batch,"ENTER - next wave  |  ESC - menu",440,375);
        game.batch.end();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) game.setScreen(new GameScreen(game));
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) game.setScreen(new MainMenuScreen(game));
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { font.dispose(); sr.dispose(); }
}
