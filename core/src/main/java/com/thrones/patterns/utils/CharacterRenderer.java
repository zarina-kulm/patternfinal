package com.thrones.patterns.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CharacterRenderer {

    public static void drawKnight(ShapeRenderer sr, float x, float y, float s, float t, boolean hit) {
        float bob = (float) Math.sin(t * 3f) * 2f * s;
        float swing = (float) Math.sin(t * 3f) * 8f * s;
        float eyeGlow = (float) (Math.sin(t * 6f) * 0.3f + 0.7f);

        Color armor = hit ? Color.ORANGE : new Color(0.22f, 0.28f, 0.42f, 1f);
        Color metal = hit ? Color.RED : new Color(0.55f, 0.62f, 0.72f, 1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Cape
        sr.setColor(new Color(0.35f, 0.03f, 0.03f, 0.9f));
        sr.triangle(x + 8 * s, y + 48 * s + bob, x - 22 * s, y + 6 * s + bob, x + 12 * s, y + 8 * s + bob);
        sr.triangle(x + 34 * s, y + 48 * s + bob, x + 58 * s, y + 8 * s + bob, x + 28 * s, y + 8 * s + bob);

        // Legs
        sr.setColor(new Color(0.16f, 0.18f, 0.25f, 1f));
        sr.rect(x + 9 * s, y + bob, 11 * s, 19 * s);
        sr.rect(x + 24 * s, y + bob, 11 * s, 19 * s);

        // Body armor
        sr.setColor(armor);
        sr.rect(x + 5 * s, y + 18 * s + bob, 36 * s, 26 * s);

        // Shoulders
        sr.setColor(new Color(0.12f, 0.14f, 0.22f, 1f));
        sr.rect(x - 2 * s, y + 33 * s + bob, 13 * s, 12 * s);
        sr.rect(x + 35 * s, y + 33 * s + bob, 13 * s, 12 * s);

        // Head / helmet
        sr.setColor(metal);
        sr.rect(x + 10 * s, y + 43 * s + bob, 26 * s, 22 * s);
        sr.setColor(new Color(0.18f, 0.20f, 0.30f, 1f));
        sr.rect(x + 8 * s, y + 58 * s + bob, 30 * s, 8 * s);

        // Eyes
        sr.setColor(new Color(1f, 0.78f, 0.18f, eyeGlow));
        sr.circle(x + 17 * s, y + 53 * s + bob, 2f * s, 8);
        sr.circle(x + 29 * s, y + 53 * s + bob, 2f * s, 8);

        // Shield
        sr.setColor(new Color(0.20f, 0.10f, 0.06f, 1f));
        sr.rect(x - 11 * s, y + 16 * s + bob, 13 * s, 31 * s);
        sr.setColor(new Color(0.85f, 0.62f, 0.12f, 1f));
        sr.rect(x - 7 * s, y + 27 * s + bob, 5 * s, 10 * s);

        // Sword glow
        sr.setColor(new Color(0.9f, 0.9f, 1f, 0.25f));
        sr.rect(x + 42 * s, y + 10 * s + bob + swing, 10 * s, 46 * s);

        // Sword
        sr.setColor(new Color(0.82f, 0.86f, 0.95f, 1f));
        sr.rect(x + 44 * s, y + 12 * s + bob + swing, 6 * s, 42 * s);
        sr.setColor(new Color(0.55f, 0.34f, 0.08f, 1f));
        sr.rect(x + 39 * s, y + 29 * s + bob + swing, 16 * s, 5 * s);

        sr.end();

        if (hit) {
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(Color.YELLOW);
            sr.circle(x + 23 * s, y + 36 * s + bob, 44 * s, 24);
            sr.end();
        }
    }

    public static void drawMage(ShapeRenderer sr, float x, float y, float s, float t, boolean hit) {
        float bob = (float) Math.sin(t * 2.5f) * 3f * s;
        float glow = (float) (Math.sin(t * 4f) * 0.5f + 0.5f);
        Color robe = hit ? Color.RED : new Color(0.27f, 0.05f, 0.46f, 1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        sr.setColor(new Color(0.15f, 0.02f, 0.25f, 0.55f));
        sr.circle(x + 22 * s, y + 34 * s + bob, 38 * s, 24);

        sr.setColor(robe);
        sr.triangle(x + 2 * s, y + bob, x + 44 * s, y + bob, x + 22 * s, y + 45 * s + bob);
        sr.rect(x + 6 * s, y + 20 * s + bob, 34 * s, 25 * s);

        sr.setColor(new Color(0.65f, 0.18f, 0.9f, 1f));
        sr.rect(x + 17 * s, y + 22 * s + bob, 6 * s, 22 * s);

        sr.setColor(new Color(0.88f, 0.75f, 0.62f, 1f));
        sr.circle(x + 22 * s, y + 55 * s + bob, 13 * s, 12);

        sr.setColor(new Color(0.22f, 0.03f, 0.36f, 1f));
        sr.triangle(x + 8 * s, y + 60 * s + bob, x + 36 * s, y + 60 * s + bob, x + 22 * s, y + 84 * s + bob);
        sr.rect(x + 6 * s, y + 57 * s + bob, 32 * s, 6 * s);

        // Staff
        sr.setColor(new Color(0.45f, 0.25f, 0.07f, 1f));
        sr.rect(x + 43 * s, y + 8 * s + bob, 4 * s, 43 * s);

        sr.setColor(new Color(0.65f + glow * 0.35f, 0.05f, 1f, 1f));
        sr.circle(x + 45 * s, y + 55 * s + bob, (7f + glow * 5f) * s, 14);

        sr.end();

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(new Color(0.7f, 0.1f, 1f, 0.7f));
        sr.circle(x + 22 * s, y + 36 * s + bob, (22 + glow * 8) * s, 24);
        sr.end();
    }

    public static void drawArcher(ShapeRenderer sr, float x, float y, float s, float t, boolean hit) {
        float bob = (float) Math.sin(t * 3.5f) * 2.5f * s;
        Color body = hit ? Color.RED : new Color(0.22f, 0.42f, 0.18f, 1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        sr.setColor(new Color(0.12f, 0.22f, 0.10f, 1f));
        sr.rect(x + 10 * s, y + bob, 10 * s, 20 * s);
        sr.rect(x + 24 * s, y + bob, 10 * s, 20 * s);

        sr.setColor(new Color(0.10f, 0.07f, 0.04f, 0.8f));
        sr.triangle(x + 8 * s, y + 42 * s + bob, x - 8 * s, y + 12 * s + bob, x + 15 * s, y + 18 * s + bob);

        sr.setColor(body);
        sr.rect(x + 8 * s, y + 20 * s + bob, 28 * s, 23 * s);

        sr.setColor(new Color(0.86f, 0.68f, 0.52f, 1f));
        sr.circle(x + 22 * s, y + 53 * s + bob, 12 * s, 12);

        sr.setColor(body);
        sr.triangle(x + 10 * s, y + 49 * s + bob, x + 34 * s, y + 49 * s + bob, x + 22 * s, y + 72 * s + bob);

        // Bow
        sr.setColor(new Color(0.45f, 0.25f, 0.08f, 1f));
        sr.rect(x - 6 * s, y + 9 * s + bob, 4 * s, 48 * s);
        sr.setColor(new Color(0.9f, 0.9f, 0.8f, 1f));
        sr.rectLine(x - 4 * s, y + 10 * s + bob, x - 4 * s, y + 57 * s + bob, 1.5f * s);

        // Arrow
        sr.setColor(new Color(0.75f, 0.75f, 0.68f, 1f));
        sr.rect(x - 10 * s, y + 34 * s + bob, 32 * s, 2 * s);

        sr.end();
    }

    public static void drawGoblin(ShapeRenderer sr, float x, float y, float s, float t, boolean hit) {
        float bob = (float) Math.sin(t * 4f) * 3f * s;
        Color skin = hit ? Color.RED : new Color(0.22f, 0.55f, 0.16f, 1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        sr.setColor(new Color(0.12f, 0.25f, 0.08f, 1f));
        sr.rect(x + 8 * s, y + bob, 8 * s, 14 * s);
        sr.rect(x + 20 * s, y + bob, 8 * s, 14 * s);

        sr.setColor(skin);
        sr.rect(x + 4 * s, y + 14 * s + bob, 28 * s, 18 * s);
        sr.circle(x + 18 * s, y + 40 * s + bob, 14 * s, 12);

        sr.triangle(x, y + 42 * s + bob, x + 8 * s, y + 36 * s + bob, x + 4 * s, y + 58 * s + bob);
        sr.triangle(x + 36 * s, y + 42 * s + bob, x + 28 * s, y + 36 * s + bob, x + 32 * s, y + 58 * s + bob);

        sr.setColor(Color.YELLOW);
        sr.circle(x + 12 * s, y + 42 * s + bob, 3 * s, 8);
        sr.circle(x + 24 * s, y + 42 * s + bob, 3 * s, 8);

        sr.setColor(Color.BLACK);
        sr.circle(x + 12 * s, y + 42 * s + bob, 1.5f * s, 6);
        sr.circle(x + 24 * s, y + 42 * s + bob, 1.5f * s, 6);

        // Dagger
        sr.setColor(new Color(0.7f, 0.7f, 0.75f, 1f));
        sr.rect(x + 34 * s, y + 18 * s + bob, 4 * s, 18 * s);

        sr.end();

        if (hit) {
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(Color.YELLOW);
            sr.circle(x + 18 * s, y + 35 * s + bob, 28 * s, 16);
            sr.end();
        }
    }

    public static void drawOrc(ShapeRenderer sr, float x, float y, float s, float t, boolean hit) {
        float bob = (float) Math.sin(t * 2.5f) * 2f * s;
        Color body = hit ? Color.RED : new Color(0.32f, 0.48f, 0.18f, 1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        sr.setColor(new Color(0.20f, 0.28f, 0.10f, 1f));
        sr.rect(x + 6 * s, y + bob, 14 * s, 23 * s);
        sr.rect(x + 28 * s, y + bob, 14 * s, 23 * s);

        sr.setColor(body);
        sr.rect(x, y + 20 * s + bob, 54 * s, 30 * s);

        sr.setColor(new Color(0.40f, 0.58f, 0.22f, 1f));
        sr.rect(x + 10 * s, y + 48 * s + bob, 34 * s, 26 * s);

        sr.setColor(new Color(0.10f, 0.08f, 0.05f, 1f));
        sr.rect(x + 2 * s, y + 38 * s + bob, 50 * s, 8 * s);

        sr.setColor(Color.WHITE);
        sr.triangle(x + 16 * s, y + 48 * s + bob, x + 20 * s, y + 48 * s + bob, x + 18 * s, y + 40 * s + bob);
        sr.triangle(x + 34 * s, y + 48 * s + bob, x + 38 * s, y + 48 * s + bob, x + 36 * s, y + 40 * s + bob);

        // Axe
        sr.setColor(new Color(0.45f, 0.32f, 0.12f, 1f));
        sr.rect(x + 54 * s, y + 12 * s + bob, 5 * s, 44 * s);

        sr.setColor(new Color(0.62f, 0.62f, 0.65f, 1f));
        sr.triangle(x + 54 * s, y + 52 * s + bob, x + 75 * s, y + 56 * s + bob, x + 54 * s, y + 35 * s + bob);

        sr.end();
    }

    public static void drawDarkKnight(ShapeRenderer sr, float x, float y, float s, float t, boolean hit) {
        float bob = (float) Math.sin(t * 2f) * 1.5f * s;
        float glow = (float) (Math.sin(t * 3f) * 0.5f + 0.5f);
        Color armor = hit ? Color.RED : new Color(0.08f, 0.08f, 0.13f, 1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Aura
        sr.setColor(0.2f, 0f, 0.3f, 0.15f + glow * 0.15f);
        sr.circle(x + 23 * s, y + 38 * s + bob, 44 * s, 24);

        sr.setColor(armor);
        sr.rect(x + 8 * s, y + bob, 12 * s, 23 * s);
        sr.rect(x + 27 * s, y + bob, 12 * s, 23 * s);
        sr.rect(x + 3 * s, y + 21 * s + bob, 40 * s, 28 * s);
        sr.rect(x - 2 * s, y + 36 * s + bob, 13 * s, 13 * s);
        sr.rect(x + 36 * s, y + 36 * s + bob, 13 * s, 13 * s);

        sr.setColor(new Color(0.03f, 0.03f, 0.06f, 1f));
        sr.rect(x + 8 * s, y + 46 * s + bob, 31 * s, 27 * s);

        sr.setColor(new Color(0.9f, 0f, 0.1f, glow));
        sr.rect(x + 12 * s, y + 63 * s + bob, 23 * s, 5 * s);

        // Sword
        sr.setColor(new Color(0.32f, 0.32f, 0.42f, 1f));
        sr.rect(x + 50 * s, y + 2 * s + bob, 7 * s, 52 * s);
        sr.setColor(new Color(0.65f, 0.1f, 0.08f, 1f));
        sr.rect(x + 45 * s, y + 30 * s + bob, 17 * s, 5 * s);

        sr.end();
    }

    public static void drawNecromancer(ShapeRenderer sr, float x, float y, float s, float t, boolean hit) {
        float bob = (float) Math.sin(t * 1.5f) * 4f * s;
        float pulse = (float) (Math.sin(t * 5f) * 0.5f + 0.5f);
        Color robe = hit ? Color.RED : new Color(0.07f, 0.03f, 0.12f, 1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        sr.setColor(new Color(0.35f, 0f, 0.55f, 0.12f + pulse * 0.15f));
        sr.circle(x + 22 * s, y + 38 * s + bob, 46 * s, 28);

        sr.setColor(robe);
        sr.triangle(x + 2 * s, y + bob, x + 46 * s, y + bob, x + 23 * s, y + 48 * s + bob);
        sr.rect(x + 4 * s, y + 22 * s + bob, 40 * s, 27 * s);

        sr.setColor(new Color(0.12f, 0.08f, 0.18f, 1f));
        sr.circle(x + 23 * s, y + 59 * s + bob, 15 * s, 12);

        // Magic orbit
        for (int i = 0; i < 3; i++) {
            float a = t * 2f + i * (float) (Math.PI * 2 / 3);
            sr.setColor(new Color(0.35f + pulse * 0.45f, 0f, 0.65f + pulse * 0.25f, 1f));
            sr.circle(
                x + 23 * s + (float) Math.cos(a) * 24 * s,
                y + 59 * s + bob + (float) Math.sin(a) * 11 * s,
                4.5f * s,
                8
            );
        }

        // Staff
        sr.setColor(new Color(0.28f, 0.18f, 0.08f, 1f));
        sr.rect(x + 48 * s, y + 5 * s + bob, 4 * s, 44 * s);

        sr.setColor(new Color(0.45f + pulse * 0.35f, 0.8f, 0.25f + pulse * 0.25f, 1f));
        sr.circle(x + 50 * s, y + 54 * s + bob, (6f + pulse * 5f) * s, 12);

        sr.end();

        sr.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < 4; i++) {
            sr.setColor(0.4f + pulse * 0.3f, 0f, 0.6f, 0.55f);
            sr.circle(x + 23 * s, y + 38 * s + bob, (18 + i * 6) * s + pulse * 4 * s, 20);
        }
        sr.end();
    }

    public static void drawDragon(ShapeRenderer sr, float x, float y, float s, float t, boolean hit) {
        float bob = (float) Math.sin(t * 1.5f) * 5f * s;
        float flap = (float) Math.sin(t * 4f);
        float fire = (float) (Math.sin(t * 6f) * 0.3f + 0.7f);

        Color body = hit
            ? new Color(1f, 0.35f, 0.05f, 1f)
            : new Color(0.22f, 0.02f, 0.02f, 1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Shadow aura
        sr.setColor(0f, 0f, 0f, 0.45f);
        sr.ellipse(x - 25 * s, y + 5 * s, 145 * s, 26 * s);

        // Wings
        sr.setColor(new Color(0.12f, 0.01f, 0.01f, 0.9f));
        sr.triangle(x + 30 * s, y + 44 * s + bob, x - 55 * s, y + 92 * s + bob + flap * 25 * s, x + 8 * s, y + 62 * s + bob);
        sr.triangle(x + 54 * s, y + 44 * s + bob, x + 138 * s, y + 92 * s + bob - flap * 25 * s, x + 75 * s, y + 62 * s + bob);

        sr.setColor(new Color(0.30f, 0.03f, 0.03f, 0.9f));
        sr.triangle(x + 25 * s, y + 42 * s + bob, x - 35 * s, y + 75 * s + bob + flap * 15 * s, x + 15 * s, y + 58 * s + bob);
        sr.triangle(x + 58 * s, y + 42 * s + bob, x + 118 * s, y + 75 * s + bob - flap * 15 * s, x + 70 * s, y + 58 * s + bob);

        // Tail
        sr.setColor(body);
        sr.triangle(x + 12 * s, y + 36 * s + bob, x - 45 * s, y + 20 * s + bob, x + 10 * s, y + 24 * s + bob);

        // Body
        sr.ellipse(x + 8 * s, y + 22 * s + bob, 65 * s, 38 * s);

        // Neck/head
        sr.setColor(new Color(0.32f, 0.03f, 0.02f, 1f));
        sr.ellipse(x + 54 * s, y + 38 * s + bob, 40 * s, 30 * s);

        // Horns
        sr.setColor(new Color(0.55f, 0.48f, 0.25f, 1f));
        sr.triangle(x + 62 * s, y + 61 * s + bob, x + 67 * s, y + 61 * s + bob, x + 57 * s, y + 80 * s + bob);
        sr.triangle(x + 77 * s, y + 61 * s + bob, x + 82 * s, y + 61 * s + bob, x + 88 * s, y + 80 * s + bob);

        // Eye
        sr.setColor(Color.YELLOW);
        sr.circle(x + 72 * s, y + 53 * s + bob, 5 * s, 8);
        sr.setColor(Color.RED);
        sr.circle(x + 72 * s, y + 53 * s + bob, 2.2f * s, 8);

        // Fire breath
        sr.setColor(new Color(1f, 0.20f, 0f, 0.85f));
        sr.ellipse(x + 90 * s, y + 38 * s + bob, 34 * s * fire, 18 * s * fire);

        sr.setColor(new Color(1f, 0.78f, 0f, 0.65f));
        sr.ellipse(x + 96 * s, y + 42 * s + bob, 20 * s * fire, 10 * s * fire);

        sr.end();

        if (hit) {
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(Color.ORANGE);
            sr.circle(x + 45 * s, y + 45 * s + bob, 78 * s, 28);
            sr.end();
        }
    }
}
