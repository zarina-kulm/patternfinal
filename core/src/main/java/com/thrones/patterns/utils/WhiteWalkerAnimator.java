package com.thrones.patterns.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class WhiteWalkerAnimator {

    public enum State {
        IDLE, RUN, ATTACK, HURT, DEAD
    }

    private Texture idle;
    private final Texture[] run = new Texture[3];
    private final Texture[] attack = new Texture[2];
    private Texture dead;

    private State currentState = State.IDLE;
    private float stateTimer = 0f;

    private static final float RUN_FRAME = 0.12f;
    private static final float ATK_FRAME = 0.11f;

    public WhiteWalkerAnimator() {
        idle = load("whitewalker_idle.png");

        run[0] = load("whitewalker_run1.png");
        run[1] = load("whitewalker_run2.png");
        run[2] = load("whitewalker_run3.png");

        attack[0] = load("whitewalker_attack1.png");
        attack[1] = load("whitewalker_attack2.png");

        dead = load("whitewalker_dead.png");
    }

    private Texture load(String name) {
        try {
            Texture texture = new Texture(Gdx.files.internal(name));
            texture.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
            );
            return texture;
        } catch (Exception e) {
            System.out.println("WHITE WALKER SPRITE NOT FOUND: " + name);
            return null;
        }
    }

    public void setState(State state) {
        if (currentState != state) {
            currentState = state;
            stateTimer = 0f;
        }
    }

    public void update(float delta) {
        stateTimer += delta;
    }

    public Texture getFrame() {
        switch (currentState) {
            case RUN:
                int runFrame = (int) (stateTimer / RUN_FRAME) % run.length;
                return run[runFrame] != null ? run[runFrame] : idle;

            case ATTACK:
                int attackFrame = (int) (stateTimer / ATK_FRAME) % attack.length;
                return attack[attackFrame] != null ? attack[attackFrame] : idle;

            case DEAD:
                return dead != null ? dead : idle;

            case HURT:
            case IDLE:
            default:
                return idle;
        }
    }

    public void dispose() {
        if (idle != null) idle.dispose();

        for (Texture texture : run) {
            if (texture != null) texture.dispose();
        }

        for (Texture texture : attack) {
            if (texture != null) texture.dispose();
        }

        if (dead != null) dead.dispose();
    }
}
