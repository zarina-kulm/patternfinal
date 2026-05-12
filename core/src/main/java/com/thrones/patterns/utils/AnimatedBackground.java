package com.thrones.patterns.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class AnimatedBackground {

    private static class Star {
        float x, y, size, twinkleTimer, twinkleSpeed;
    }

    private static class Particle {
        float x, y, vx, vy, life, maxLife, size;
        Color color;
    }

    private final List<Star> stars = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();
    private final Random rand = new Random();

    public AnimatedBackground(int starCount) {
        for (int i = 0; i < starCount; i++) {
            Star s = new Star();
            s.x = rand.nextFloat() * 1280;
            s.y = rand.nextFloat() * 720;
            s.size = rand.nextFloat() * 2.5f + 0.5f;
            s.twinkleSpeed = rand.nextFloat() * 2f + 0.5f;
            stars.add(s);
        }
    }

    public void spawnParticle(float x, float y, Color color) {
        for (int i = 0; i < 8; i++) {
            Particle p = new Particle();
            p.x = x; p.y = y;
            p.vx = (rand.nextFloat() - 0.5f) * 120f;
            p.vy = (rand.nextFloat() - 0.5f) * 120f;
            p.maxLife = rand.nextFloat() * 0.8f + 0.3f;
            p.life = p.maxLife;
            p.size = rand.nextFloat() * 6f + 2f;
            p.color = color.cpy();
            particles.add(p);
        }
    }

    public void update(float delta) {
        for (Star s : stars) s.twinkleTimer += delta * s.twinkleSpeed;
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.x += p.vx * delta;
            p.y += p.vy * delta;
            p.vy -= 60f * delta;
            p.life -= delta;
            if (p.life <= 0) it.remove();
        }
    }

    public void render(ShapeRenderer sr) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.03f, 0.02f, 0.08f, 1f);
        sr.rect(0, 0, 1280, 720);

        for (Star s : stars) {
            float t = (float)(Math.sin(s.twinkleTimer) * 0.5 + 0.5);
            float a = 0.3f + t * 0.7f;
            sr.setColor(a, a, a * 0.9f + 0.1f, 1f);
            sr.circle(s.x, s.y, s.size * (0.8f + t * 0.4f), 6);
        }

        for (Particle p : particles) {
            float a = p.life / p.maxLife;
            sr.setColor(p.color.r, p.color.g, p.color.b, a);
            sr.circle(p.x, p.y, p.size * a, 8);
        }
        sr.end();
    }
}
