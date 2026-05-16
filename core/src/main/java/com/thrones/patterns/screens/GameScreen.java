package com.thrones.patterns.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.thrones.patterns.WarOfRealms;
import com.thrones.patterns.characters.Hero;
import com.thrones.patterns.enemies.Enemy;
import com.thrones.patterns.patterns.builder.*;
import com.thrones.patterns.patterns.facade.BattleFacade;
import com.thrones.patterns.patterns.factory.HeroFactory;
import com.thrones.patterns.patterns.prototype.EnemyPrototypeRegistry;
import com.thrones.patterns.patterns.singleton.GameStateSingleton;
import com.thrones.patterns.utils.AnimatedBackground;
import com.thrones.patterns.utils.CharacterRenderer;
import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private final WarOfRealms game;
    private final BattleFacade facade;
    private final GameStateSingleton state;

    private Hero hero;
    private List<Enemy> enemies;
    private int targetIndex = 0;

    private ShapeRenderer sr;
    private BitmapFont font;
    private AnimatedBackground bg;

    private float time = 0;
    private float attackTimer = 0;
    private float healCD = 0, fireCD = 0, tauntCD = 0, dodgeCD = 0;
    private boolean dodgeActive = false;
    private float dodgeTimer = 0;

    private String message = "";
    private float msgTimer = 0;
    private Color msgColor = Color.YELLOW;

    private boolean heroHit = false;
    private float heroHitTimer = 0;
    private boolean[] eHit;
    private float[] eHitTimer;

    private static final float GROUND = 180f;
    private static final float ATK_SPD = 1f;
    private static final float HEAL_CD = 15f;
    private static final float FIRE_CD = 8f;
    private static final float TAUNT_CD = 12f;
    private static final float DODGE_CD = 10f;

    public GameScreen(WarOfRealms game) {
        this.game = game;
        this.facade = new BattleFacade();
        this.state = GameStateSingleton.getInstance();
        this.enemies = new ArrayList<>();
        this.sr = new ShapeRenderer();
        this.font = new BitmapFont();
        this.bg = new AnimatedBackground(120);
        setup();
    }

    private void setup() {
        hero = HeroFactory.createHero(state.getSelectedHeroType(), "House of Players");
        hero.setPosition(120, GROUND);

        EnemyPrototypeRegistry reg = EnemyPrototypeRegistry.getInstance();
        enemies.add(reg.spawn("GOBLIN"));
        enemies.add(reg.spawn("GOBLIN"));
        if (state.getWave() >= 2) enemies.add(reg.spawn("ORC"));
        if (state.getWave() >= 3) enemies.add(reg.spawn("DARK_KNIGHT"));
        if (state.getWave() >= 4) enemies.add(reg.spawn("NECROMANCER"));
        if (state.getWave() >= 5) enemies.add(reg.spawn("DRAGON"));

        float[] xs = {750,900,820,970,860,700};
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).setPosition(xs[Math.min(i,xs.length-1)], GROUND);
            enemies.get(i).setTarget(hero);
        }

        eHit = new boolean[enemies.size()];
        eHitTimer = new float[enemies.size()];

        BattleConfig cfg = new BattleConfigBuilder()
            .setBattleName("Wave " + state.getWave())
            .setHero(hero)
            .setWaveCount(state.getWave())
            .build();
        facade.setupBattle(cfg);

        msg("Wave " + state.getWave() + " — FIGHT!", Color.GOLD);
    }

    private void msg(String m, Color c) { message=m; msgTimer=2.5f; msgColor=c; }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        time += delta;
        update(delta);

        bg.update(delta);
        bg.render(sr);
        drawScene();
        drawCharacters();

        game.batch.begin();
        drawHUD();
        drawControls();
        if (msgTimer > 0) {
            font.getData().setScale(1.4f);
            font.setColor(msgColor.r, msgColor.g, msgColor.b, Math.min(1f, msgTimer));
            font.draw(game.batch, message, 400, 420);
            font.getData().setScale(1f);
        }
        game.batch.end();

        drawHPBars();
        drawTarget();

        if (facade.isBattleOver(hero, enemies)) {
            if (state.isGameOver()) game.setScreen(new GameOverScreen(game));
            else { state.nextWave(); game.setScreen(new VictoryScreen(game)); }
        }
    }

    private void update(float delta) {
        hero.update(delta);
        for (Enemy e : enemies) e.update(delta);

        if (healCD>0) healCD-=delta;
        if (fireCD>0) fireCD-=delta;
        if (tauntCD>0) tauntCD-=delta;
        if (dodgeCD>0) dodgeCD-=delta;
        if (msgTimer>0) msgTimer-=delta;
        if (dodgeActive) { dodgeTimer-=delta; if(dodgeTimer<=0) dodgeActive=false; }
        if (heroHitTimer>0) { heroHitTimer-=delta; heroHit=heroHitTimer>0; }
        for (int i=0;i<eHitTimer.length;i++) {
            if (eHitTimer[i]>0) { eHitTimer[i]-=delta; eHit[i]=eHitTimer[i]>0; }
        }

        attackTimer += delta;
        if (attackTimer >= ATK_SPD) {
            attackTimer = 0;
            Enemy t = getTarget();
            if (t != null) {
                int idx = enemies.indexOf(t);
                facade.heroAttacks(hero, t);
                if (idx>=0 && idx<eHit.length) {
                    eHit[idx]=true; eHitTimer[idx]=0.15f;
                    bg.spawnParticle(t.getX()+32, t.getY()+40, Color.RED);
                }
            }
        }

        if (!dodgeActive) {
            for (Enemy e : enemies) {
                if (e.isAlive()) facade.enemyAttacks(e, hero);
            }
        }

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            hero.useSpecialAbility();
            Enemy t = getTarget();
            if (t!=null) { t.takeDamage(hero.getAttack()*2f);
                bg.spawnParticle(t.getX()+32,t.getY()+40,Color.PURPLE); }
            msg("[Q] "+hero.getSpecialAbilityName()+"!", Color.CYAN);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (healCD<=0) { hero.heal(30f); healCD=HEAL_CD;
                bg.spawnParticle(hero.getX()+32,hero.getY()+40,Color.GREEN);
                msg("[W] Healed +30 HP!", Color.GREEN);
            } else msg("Heal: "+(int)healCD+"s", Color.GRAY);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (fireCD<=0) {
                Enemy t=getTarget();
                if (t!=null) { float d=hero.getAttack()*2.5f; t.takeDamage(d);
                    bg.spawnParticle(t.getX()+32,t.getY()+40,Color.ORANGE);
                    msg("[E] Fire Attack! -"+(int)d+" DMG!", Color.ORANGE); }
                fireCD=FIRE_CD;
            } else msg("Fire: "+(int)fireCD+"s", Color.GRAY);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (tauntCD<=0) {
                int n=0;
                for (Enemy e:enemies) if(e.isAlive()) {
                    e.takeDamage(hero.getAttack()*0.5f);
                    bg.spawnParticle(e.getX()+32,e.getY()+40,Color.RED); n++; }
                tauntCD=TAUNT_CD;
                msg("[R] TAUNT! Hit "+n+" enemies!", Color.RED);
            } else msg("Taunt: "+(int)tauntCD+"s", Color.GRAY);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (dodgeCD<=0) { dodgeActive=true; dodgeTimer=2f; dodgeCD=DODGE_CD;
                bg.spawnParticle(hero.getX()+32,hero.getY()+40,Color.CYAN);
                msg("[SPC] DODGE! Immune 2s!", Color.CYAN);
            } else msg("Dodge: "+(int)dodgeCD+"s", Color.GRAY);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) sel(0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) sel(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) sel(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) sel(3);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) sel(4);
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            nextTarget();
            Enemy t=getTarget();
            if(t!=null) msg("Target: "+t.getName(), Color.YELLOW);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            game.setScreen(new MainMenuScreen(game));
    }

    private void sel(int i) {
        if (i<enemies.size()&&enemies.get(i).isAlive()) {
            targetIndex=i; msg("Target: "+enemies.get(i).getName(), Color.YELLOW); }
    }
    private void nextTarget() {
        for (int i=1;i<=enemies.size();i++) {
            int n=(targetIndex+i)%enemies.size();
            if(enemies.get(n).isAlive()){targetIndex=n;return;}
        }
    }
    private Enemy getTarget() {
        if (targetIndex<enemies.size()&&enemies.get(targetIndex).isAlive())
            return enemies.get(targetIndex);
        for (int i=0;i<enemies.size();i++)
            if(enemies.get(i).isAlive()){targetIndex=i;return enemies.get(i);}
        return null;
    }

    private void drawScene() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.12f,0.08f,0.04f,1f);
        sr.rect(0,0,1280,GROUND);
        sr.setColor(0.2f,0.15f,0.08f,1f);
        sr.rect(0,GROUND-4,1280,8);

        sr.setColor(0.06f,0.04f,0.1f,1f);
        sr.rect(100,GROUND,60,180); sr.rect(220,GROUND,40,140);
        sr.rect(900,GROUND,60,160); sr.rect(1050,GROUND,50,130);
        sr.rect(100,GROUND+120,160,30); sr.rect(900,GROUND+100,160,25);
        for(int i=0;i<4;i++) sr.rect(100+i*16,GROUND+176,10,14);
        sr.end();
    }

    private void drawCharacters() {
        switch (hero.getType()) {
            case "KNIGHT": CharacterRenderer.drawKnight(sr,hero.getX(),hero.getY(),1f,time,heroHit); break;
            case "MAGE":   CharacterRenderer.drawMage(sr,hero.getX(),hero.getY(),1f,time,heroHit); break;
            case "ARCHER": CharacterRenderer.drawArcher(sr,hero.getX(),hero.getY(),1f,time,heroHit); break;
        }

        for (int i=0;i<enemies.size();i++) {
            Enemy e=enemies.get(i);
            if(!e.isAlive()) continue;
            boolean h=i<eHit.length&&eHit[i];
            switch(e.getType()) {
                case "GOBLIN":      CharacterRenderer.drawGoblin(sr,e.getX(),e.getY(),0.9f,time,h); break;
                case "ORC":         CharacterRenderer.drawOrc(sr,e.getX(),e.getY(),0.85f,time,h); break;
                case "DARK_KNIGHT": CharacterRenderer.drawDarkKnight(sr,e.getX(),e.getY(),0.95f,time,h); break;
                case "NECROMANCER": CharacterRenderer.drawNecromancer(sr,e.getX(),e.getY(),0.9f,time,h); break;
                case "DRAGON":      CharacterRenderer.drawDragon(sr,e.getX()-20,e.getY(),0.8f,time,h); break;
            }
        }

        if (dodgeActive) {
            sr.begin(ShapeRenderer.ShapeType.Line);
            float p=(float)(Math.sin(time*10f)*0.5+0.5);
            sr.setColor(0f,p,p,1f);
            sr.circle(hero.getX()+24,hero.getY()+36,40,20);
            sr.end();
        }
    }

    private void drawHUD() {
        font.getData().setScale(1f);
        font.setColor(Color.GOLD);
        font.draw(game.batch,"THRONES OF PATTERNS",450,714);
        font.getData().setScale(0.9f);
        font.setColor(Color.WHITE);
        font.draw(game.batch,"Wave: "+state.getWave(),20,695);
        font.setColor(Color.YELLOW);
        font.draw(game.batch,"Gold: "+state.getGold(),20,672);
        font.setColor(Color.CYAN);
        font.draw(game.batch,"Score: "+state.getScore(),20,649);
        font.setColor(Color.WHITE);
        font.draw(game.batch,hero.getName()+" Lv."+hero.getLevel(),20,626);
        float hPct=hero.getHp()/hero.getMaxHp();
        font.setColor(hPct>0.5f?Color.GREEN:hPct>0.25f?Color.ORANGE:Color.RED);
        font.draw(game.batch,"HP: "+(int)hero.getHp()+"/"+(int)hero.getMaxHp(),20,603);
        if(dodgeActive){font.setColor(Color.CYAN);font.draw(game.batch,"DODGING!",20,580);}

        font.setColor(Color.LIGHT_GRAY);
        font.draw(game.batch,"ENEMIES:",1050,695);
        for(int i=0;i<enemies.size();i++){
            Enemy e=enemies.get(i);
            font.setColor(i==targetIndex&&e.isAlive()?Color.YELLOW:e.isAlive()?Color.WHITE:Color.DARK_GRAY);
            font.draw(game.batch,(i+1)+". "+e.getName()+(e.isAlive()?" "+(int)e.getHp():" [X]"),1050,672-i*22);
        }
        font.getData().setScale(1f);
    }

    private void drawControls() {
        font.getData().setScale(0.82f);
        int y=155, s=20;
        cd("[Q]",hero.getSpecialAbilityName(),0,Color.CYAN,y);
        cd("[W]","Heal +30",healCD,Color.GREEN,y-s);
        cd("[E]","Fire 2.5x",fireCD,Color.ORANGE,y-s*2);
        cd("[R]","Taunt All",tauntCD,Color.RED,y-s*3);
        cd("[SPC]","Dodge 2s",dodgeCD,Color.CYAN,y-s*4);
        font.setColor(Color.DARK_GRAY);
        font.draw(game.batch,"[1-5] Target  [TAB] Next  [ESC] Menu",20,y-s*5-4);
        font.getData().setScale(1f);
    }

    private void cd(String key, String name, float c, Color col, float y) {
        if(c>0){font.setColor(Color.GRAY);font.draw(game.batch,key+" "+name+" ("+(int)c+"s)",20,y);}
        else{font.setColor(col);font.draw(game.batch,key+" "+name+" [READY]",20,y);}
    }

    private void drawHPBars() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        float hp=hero.getHp()/hero.getMaxHp();
        sr.setColor(0.15f,0.15f,0.15f,1f);
        sr.rect(hero.getX(),hero.getY()+80,80,10);
        sr.setColor(hp>0.5f?Color.GREEN:hp>0.25f?Color.ORANGE:Color.RED);
        sr.rect(hero.getX(),hero.getY()+80,80*hp,10);
        for(Enemy e:enemies){
            if(!e.isAlive()) continue;
            float ep=e.getHp()/e.getMaxHp();
            sr.setColor(0.15f,0.15f,0.15f,1f);
            sr.rect(e.getX(),e.getY()+78,64,8);
            sr.setColor(Color.RED);
            sr.rect(e.getX(),e.getY()+78,64*ep,8);
        }
        sr.end();
    }

    private void drawTarget() {
        if(targetIndex<enemies.size()&&enemies.get(targetIndex).isAlive()){
            Enemy t=enemies.get(targetIndex);
            float p=(float)(Math.sin(time*4f)*0.4+0.6);
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(p,p*0.8f,0f,1f);
            sr.rect(t.getX()-4,t.getY()-4,72,90);
            sr.triangle(t.getX()+28,t.getY()+95,t.getX()+36,t.getY()+110,t.getX()+44,t.getY()+95);
            sr.end();
        }
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { sr.dispose(); font.dispose(); }
}
