package com.thrones.patterns.patterns.objectpool;

public class Projectile{
    private float x, y, vx, vy, damage, lifetime;
    private boolean active;
    private String type;

    public Projectile(){ 
        reset(); 
    }
    public void activate(float x, float y, float vx, float vy, float damage, String type){
        this.x=x; this.y=y;
        this.vx=vx; this.vy=vy;
        this.damage=damage; this.type=type;
        this.active=true; this.lifetime=3f;
    }

    public void update(float delta){
        if(!active) return;
        x += vx*delta; y += vy*delta;
        lifetime-=delta;
        if(lifetime<=0) active=false;
    }

    public void reset(){
        x=0; y=0; vx=0; vy=0;
        damage=0; active=false; type="ARROW"; lifetime=0;
    }

    public boolean isActive(){ 
        return active; 
    }
    public float getX(){ 
        return x; 
    }
    public float getY(){ 
        return y; 
    }
    public float getDamage(){ 
        return damage; 
    }
    public void deactivate(){ 
        active=false; 
    }
}