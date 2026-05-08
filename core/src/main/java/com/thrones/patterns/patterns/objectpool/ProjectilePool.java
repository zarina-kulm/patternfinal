package com.thrones.patterns.patterns.objectpool;

import java.util.ArrayDeque;
import java.util.Deque;

public class ProjectilePool{

    private static ProjectilePool instance;
    private final Deque<Projectile> pool;

    private ProjectilePool(){
        pool=new ArrayDeque<>();
        for(int i=0; i<30; i++) pool.push(new Projectile());
    }

    public static ProjectilePool getInstance(){
        if(instance==null) instance=new ProjectilePool();
        return instance;
    }

    public Projectile obtain(){
        return pool.isEmpty() ? new Projectile() : pool.pop();
    }

    public void free(Projectile p){
        p.reset();
        pool.push(p);
    }

    public int size(){ 
        return pool.size(); 
    }
}