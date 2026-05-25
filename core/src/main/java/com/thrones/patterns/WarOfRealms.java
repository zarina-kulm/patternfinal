package com.thrones.patterns;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thrones.patterns.patterns.singleton.GameStateSingleton;
import com.thrones.patterns.screens.MainMenuScreen;

public class WarOfRealms extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        GameStateSingleton.getInstance().init();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }

        super.dispose();
    }
}
