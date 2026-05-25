package com.thrones.patterns.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class KnightAnimator {

    public enum State { IDLE, RUN, ATTACK, HURT, DEAD, JUMP }

    private Texture idle;
    private Texture[] run = new Texture[4];
    private Texture[] attack = new Texture[3];
    private Texture hurt;
    private Texture dead;
    private Texture jump;

    private State currentState = State.IDLE;
    private float stateTimer = 0f;

    private static final float RUN_FRAME_TIME = 0.12f;
    private static final float ATTACK_FRAME_TIME = 0.1f;

    public KnightAnimator() {
        idle = load("knight_idle.png");
        for (int i = 0; i < 4; i++)
            run[i] = load("knight_run" + (i+1) + ".png");
        for (int i = 0; i < 3; i++)
            attack[i] = load("knight_attack" + (i+1) + ".png");
        hurt = load("knight_hurt.png");
        dead = load("knight_dead.png");
        jump = load("knight_jump.png");
    }

    private Texture load(String name) {
        try {
            return new Texture(Gdx.files.internal(name));
        } catch (Exception e) {
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

    public Texture getCurrentFrame() {
        switch (currentState) {
            case RUN:
                int runFrame = (int)(stateTimer / RUN_FRAME_TIME) % 4;
                return run[runFrame] != null ? run[runFrame] : idle;

            case ATTACK:
                int attFrame = (int)(stateTimer / ATTACK_FRAME_TIME);
                if (attFrame >= 3) attFrame = 2;
                return attack[attFrame] != null ? attack[attFrame] : idle;

            case HURT:
                return hurt != null ? hurt : idle;

            case DEAD:
                return dead != null ? dead : idle;

            case JUMP:
                return jump != null ? jump : idle;

            case IDLE:
            default:
                return idle;
        }
    }

    public boolean isAttackDone() {
        return currentState == State.ATTACK &&
            stateTimer >= ATTACK_FRAME_TIME * 3;
    }

    public void dispose() {
        if (idle != null) idle.dispose();
        for (Texture t : run) if (t != null) t.dispose();
        for (Texture t : attack) if (t != null) t.dispose();
        if (hurt != null) hurt.dispose();
        if (dead != null) dead.dispose();
        if (jump != null) jump.dispose();
    }
}
