package com.thrones.patterns.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CharacterRenderer {

    public static void drawKnight(ShapeRenderer sr, float x, float y,
                                  float s, float t, boolean hit) {
        float bob = (float)Math.sin(t * 3f) * 2f * s;
        Color body = hit ? Color.RED : new Color(0.5f, 0.6f, 0.8f, 1f);
        Color armor = hit ? Color.ORANGE : new Color(0.3f, 0.4f, 0.6f, 1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(armor);
        sr.rect(x+10*s, y+bob, 10*s, 18*s);
        sr.rect(x+24*s, y+bob, 10*s, 18*s);
        sr.setColor(body);
        sr.rect(x+6*s, y+18*s+bob, 32*s, 22*s);
        sr.setColor(armor);
        sr.rect(x, y+32*s+bob, 10*s, 10*s);
        sr.rect(x+34*s, y+32*s+bob, 10*s, 10*s);
        sr.setColor(new Color(0.7f,0.7f,0.7f,1f));
        sr.rect(x+10*s, y+40*s+bob, 24*s, 22*s);
        sr.setColor(armor);
        sr.rect(x+8*s, y+56*s+bob, 28*s, 8*s);
        // Қалқан
        sr.setColor(new Color(0.4f,0.2f,0.1f,1f));
        sr.rect(x-8*s, y+16*s+bob, 10*s, 28*s);
        sr.setColor(new Color(0.8f,0.6f,0.1f,1f));
        sr.rect(x-5*s, y+26*s+bob, 4*s, 8*s);
        // Қылыш
        float swing = (float)Math.sin(t*3f)*8f*s;
        sr.setColor(new Color(0.8f,0.8f,0.9f,1f));
        sr.rect(x+44*s, y+20*s+bob+swing, 4*s, 30*s);
        sr.setColor(new Color(0.6f,0.4f,0.1f,1f));
        sr.rect(x+40*s, y+28*s+bob+swing, 12*s, 4*s);
        sr.end();

        if (hit) {
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(Color.YELLOW);
            sr.rect(x-4*s, y-4*s, 56*s, 76*s);
            sr.end();
        }
    }

    public static void drawMage(ShapeRenderer sr, float x, float y,
                                float s, float t, boolean hit) {
        float bob = (float)Math.sin(t*2.5f)*3f*s;
        float glow = (float)(Math.sin(t*4f)*0.5+0.5);
        Color robe = hit ? Color.RED : new Color(0.4f,0.1f,0.6f,1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(robe);
        sr.triangle(x+4*s, y+bob, x+40*s, y+bob, x+22*s, y+40*s+bob);
        sr.rect(x+6*s, y+18*s+bob, 32*s, 24*s);
        sr.setColor(new Color(0.5f,0.2f,0.7f,1f));
        sr.rect(x, y+22*s+bob, 8*s, 18*s);
        sr.rect(x+36*s, y+22*s+bob, 8*s, 18*s);
        sr.setColor(new Color(0.9f,0.8f,0.7f,1f));
        sr.circle(x+22*s, y+52*s+bob, 14*s, 12);
        sr.setColor(new Color(0.3f,0.1f,0.5f,1f));
        sr.triangle(x+8*s, y+58*s+bob, x+36*s, y+58*s+bob, x+22*s, y+82*s+bob);
        sr.rect(x+6*s, y+56*s+bob, 32*s, 6*s);
        // Жезл
        sr.setColor(new Color(0.6f,0.4f,0.1f,1f));
        sr.rect(x+40*s, y+10*s+bob, 4*s, 36*s);
        sr.setColor(new Color(0.5f+glow*0.5f, 0.1f, 0.8f+glow*0.2f, 1f));
        sr.circle(x+42*s, y+50*s+bob, (6f+glow*4f)*s, 12);
        sr.end();
    }

    public static void drawArcher(ShapeRenderer sr, float x, float y,
                                  float s, float t, boolean hit) {
        float bob = (float)Math.sin(t*3.5f)*2.5f*s;
        Color body = hit ? Color.RED : new Color(0.3f,0.5f,0.2f,1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(new Color(0.2f,0.35f,0.15f,1f));
        sr.rect(x+10*s, y+bob, 10*s, 20*s);
        sr.rect(x+24*s, y+bob, 10*s, 20*s);
        sr.setColor(body);
        sr.rect(x+8*s, y+20*s+bob, 28*s, 22*s);
        sr.setColor(new Color(0.9f,0.75f,0.6f,1f));
        sr.circle(x+22*s, y+52*s+bob, 12*s, 12);
        sr.setColor(body);
        sr.triangle(x+10*s, y+48*s+bob, x+34*s, y+48*s+bob, x+22*s, y+70*s+bob);
        // Садақ
        sr.setColor(new Color(0.5f,0.3f,0.1f,1f));
        sr.rect(x-4*s, y+10*s+bob, 4*s, 44*s);
        sr.setColor(new Color(0.9f,0.9f,0.8f,1f));
        sr.rectLine(x-2*s, y+10*s+bob, x-2*s, y+54*s+bob, 1.5f);
        sr.end();
    }

    public static void drawGoblin(ShapeRenderer sr, float x, float y,
                                  float s, float t, boolean hit) {
        float bob = (float)Math.sin(t*4f)*3f*s;
        Color skin = hit ? Color.RED : new Color(0.3f,0.6f,0.2f,1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(new Color(0.2f,0.4f,0.1f,1f));
        sr.rect(x+8*s, y+bob, 8*s, 14*s);
        sr.rect(x+20*s, y+bob, 8*s, 14*s);
        sr.setColor(skin);
        sr.rect(x+4*s, y+14*s+bob, 28*s, 18*s);
        sr.circle(x+18*s, y+40*s+bob, 14*s, 12);
        // Құлақтар
        sr.triangle(x, y+42*s+bob, x+8*s, y+36*s+bob, x+4*s, y+56*s+bob);
        sr.triangle(x+36*s, y+42*s+bob, x+28*s, y+36*s+bob, x+32*s, y+56*s+bob);
        // Көздер
        sr.setColor(Color.YELLOW);
        sr.circle(x+12*s, y+42*s+bob, 3*s, 8);
        sr.circle(x+24*s, y+42*s+bob, 3*s, 8);
        sr.setColor(Color.BLACK);
        sr.circle(x+12*s, y+42*s+bob, 1.5f*s, 6);
        sr.circle(x+24*s, y+42*s+bob, 1.5f*s, 6);
        sr.end();
    }

    public static void drawOrc(ShapeRenderer sr, float x, float y,
                               float s, float t, boolean hit) {
        float bob = (float)Math.sin(t*2.5f)*2f*s;
        Color body = hit ? Color.RED : new Color(0.4f,0.55f,0.2f,1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(new Color(0.3f,0.4f,0.15f,1f));
        sr.rect(x+6*s, y+bob, 14*s, 22*s);
        sr.rect(x+26*s, y+bob, 14*s, 22*s);
        sr.setColor(body);
        sr.rect(x, y+20*s+bob, 52*s, 28*s);
        sr.setColor(new Color(0.45f,0.6f,0.25f,1f));
        sr.rect(x+10*s, y+46*s+bob, 32*s, 26*s);
        // Азулар
        sr.setColor(Color.WHITE);
        sr.triangle(x+16*s, y+46*s+bob, x+20*s, y+46*s+bob, x+18*s, y+40*s+bob);
        sr.triangle(x+32*s, y+46*s+bob, x+36*s, y+46*s+bob, x+34*s, y+40*s+bob);
        // Балта
        sr.setColor(new Color(0.5f,0.5f,0.5f,1f));
        sr.rect(x+52*s, y+18*s+bob, 5*s, 32*s);
        sr.setColor(new Color(0.7f,0.3f,0.1f,1f));
        sr.triangle(x+52*s, y+46*s+bob, x+68*s, y+50*s+bob, x+52*s, y+30*s+bob);
        sr.end();
    }

    public static void drawDarkKnight(ShapeRenderer sr, float x, float y,
                                      float s, float t, boolean hit) {
        float bob = (float)Math.sin(t*2f)*1.5f*s;
        float glow = (float)(Math.sin(t*3f)*0.5+0.5);
        Color armor = hit ? Color.RED : new Color(0.15f,0.15f,0.2f,1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(armor);
        sr.rect(x+8*s, y+bob, 12*s, 22*s);
        sr.rect(x+26*s, y+bob, 12*s, 22*s);
        sr.rect(x+4*s, y+20*s+bob, 38*s, 26*s);
        sr.rect(x, y+34*s+bob, 12*s, 12*s);
        sr.rect(x+34*s, y+34*s+bob, 12*s, 12*s);
        sr.setColor(new Color(0.1f,0.1f,0.15f,1f));
        sr.rect(x+8*s, y+44*s+bob, 30*s, 26*s);
        sr.setColor(new Color(glow*0.8f, 0, glow*0.5f, 1f));
        sr.rect(x+10*s, y+60*s+bob, 26*s, 6*s);
        // Қылыш
        sr.setColor(new Color(0.4f,0.4f,0.5f,1f));
        sr.rect(x+48*s, y+4*s+bob, 6*s, 48*s);
        sr.setColor(new Color(0.7f,0.3f,0.1f,1f));
        sr.rect(x+44*s, y+28*s+bob, 14*s, 5*s);
        sr.end();
    }

    public static void drawNecromancer(ShapeRenderer sr, float x, float y,
                                       float s, float t, boolean hit) {
        float bob = (float)Math.sin(t*1.5f)*4f*s;
        float pulse = (float)(Math.sin(t*5f)*0.5+0.5);
        Color robe = hit ? Color.RED : new Color(0.1f,0.05f,0.15f,1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(robe);
        sr.triangle(x+2*s, y+bob, x+44*s, y+bob, x+22*s, y+44*s+bob);
        sr.rect(x+4*s, y+20*s+bob, 38*s, 26*s);
        sr.setColor(new Color(0.15f,0.1f,0.2f,1f));
        sr.circle(x+22*s, y+56*s+bob, 14*s, 12);
        // Орбита
        for (int i = 0; i < 3; i++) {
            float a = t*2f + i*(float)(Math.PI*2/3);
            sr.setColor(new Color(0.3f+pulse*0.4f, 0, 0.5f+pulse*0.3f, 1f));
            sr.circle(x+22*s+(float)Math.cos(a)*22*s,
                y+56*s+bob+(float)Math.sin(a)*10*s, 4*s, 8);
        }
        // Жезл
        sr.setColor(new Color(0.3f,0.2f,0.1f,1f));
        sr.rect(x+46*s, y+6*s+bob, 4*s, 40*s);
        sr.setColor(new Color(0.5f+pulse*0.3f, 0.8f, 0.3f+pulse*0.2f, 1f));
        sr.circle(x+48*s, y+50*s+bob, (5f+pulse*5f)*s, 10);
        sr.end();
    }

    public static void drawDragon(ShapeRenderer sr, float x, float y,
                                  float s, float t, boolean hit) {
        float bob = (float)Math.sin(t*1.5f)*5f*s;
        float flap = (float)Math.sin(t*4f);
        float fire = (float)(Math.sin(t*6f)*0.3+0.7);
        Color body = hit ? Color.ORANGE : new Color(0.6f,0.1f,0.05f,1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        // Қанаттар
        sr.setColor(new Color(0.4f,0.05f,0.05f,0.8f));
        sr.triangle(x+30*s, y+40*s+bob,
            x-40*s, y+80*s+bob+flap*20*s,
            x+10*s, y+60*s+bob);
        sr.triangle(x+50*s, y+40*s+bob,
            x+120*s, y+80*s+bob-flap*20*s,
            x+70*s, y+60*s+bob);
        // Дене
        sr.setColor(body);
        sr.ellipse(x+10*s, y+20*s+bob, 60*s, 36*s);
        sr.setColor(new Color(0.65f,0.12f,0.06f,1f));
        sr.ellipse(x+52*s, y+36*s+bob, 36*s, 28*s);
        // Мүйіздер
        sr.setColor(new Color(0.3f,0.3f,0.1f,1f));
        sr.triangle(x+60*s, y+58*s+bob, x+64*s, y+58*s+bob, x+56*s, y+74*s+bob);
        sr.triangle(x+72*s, y+58*s+bob, x+76*s, y+58*s+bob, x+80*s, y+74*s+bob);
        // Көздер
        sr.setColor(Color.YELLOW);
        sr.circle(x+68*s, y+50*s+bob, 5*s, 8);
        sr.setColor(Color.BLACK);
        sr.circle(x+68*s, y+50*s+bob, 2.5f*s, 6);
        // Өрт
        sr.setColor(new Color(1f,0.5f,0f,0.9f));
        sr.circle(x+88*s, y+46*s+bob, 8*s*fire, 10);
        sr.setColor(new Color(1f,0.8f,0f,0.6f));
        sr.circle(x+88*s, y+46*s+bob, 5*s*fire, 8);
        sr.end();
    }
}
