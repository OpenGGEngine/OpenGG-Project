/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.particle;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.InstancedDrawnObject;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.util.Time;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.Positioned;
import com.opengg.core.world.components.Renderable;
import com.opengg.core.world.components.Updatable;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Warren
 * 
 * This represents a group of particles
 */
public class ParticleSystem implements Updatable, Renderable{
    List<Particle> particles = new LinkedList<>();
    private Vector3f offset = new Vector3f();
    private Vector3f scale = new Vector3f(1,1,1);
    private Quaternionf rotoffset = new Quaternionf();
    InstancedDrawnObject particleobject;
    private float pps;
    private float speed;
    private float gravityComplient;
    private float lifeLength;
    private float timeSinceLast = 0f;
    Positioned p;
    Time time;
    Texture t;

    public ParticleSystem(float pps, float speed, float gravityComplient, float lifeLength,FloatBuffer model, Texture t) {
        this.pps = pps;
        this.speed = speed;
        this.gravityComplient = gravityComplient;
        this.lifeLength = lifeLength;
        particleobject = new InstancedDrawnObject(model,2,createParticleVBO());
        time = new Time();
        this.t = t;
    }
    
    public ParticleSystem(float pps, float speed, float lifeLength,FloatBuffer model, Texture t) {
        this.pps = pps;
        this.speed = speed;
        this.gravityComplient = -0.027f;
        this.lifeLength = lifeLength;
        particleobject = new InstancedDrawnObject(model,2,createParticleVBO());
        time = new Time();
        this.t = t;
    }
    
    public ParticleSystem(float pps, float speed, float lifeLength, InstancedDrawnObject model, Texture t) {
        this.pps = pps;
        this.speed = speed;
        this.gravityComplient = -0.027f;
        this.lifeLength = lifeLength;
        this.particleobject = model;
        time = new Time();
        this.t = t;
    }
    
    private void emitParticle(Vector3f center){
        float dirX = (float) Math.random() * 2f - 1f;
        float dirZ = (float) Math.random() * 2f - 1f;
        Vector3f velocity = new Vector3f(dirX, 1, dirZ);
        velocity.normalize();
        velocity.multiply(speed);
        particles.add(new Particle(center, velocity, new Vector3f(0,gravityComplient,0), lifeLength,1f));
    }
    
    @Override
    public void update(float delta) {
        Vector3f position = offset.add(p.getPosition());
        timeSinceLast += delta;
        if(timeSinceLast >= (1/pps)*1000){
            timeSinceLast = 0;
            emitParticle(position);
        }
        
        Iterator<Particle> screwconcurrent = particles.iterator();
        while(screwconcurrent.hasNext()){
            Particle p2 = screwconcurrent.next();
            if(p2.update()){
                screwconcurrent.remove();             
            }
            
        }
        particleobject.setPositions(createParticleVBO(),particles.size());
    }
    @Override
    public void render(){
        t.useTexture(0);
        particleobject.draw();
    }
    private FloatBuffer createParticleVBO(){
        Vector3f[] vs = new Vector3f[particles.size()];
        int i = 0;
        for(Particle ps : particles){
            vs[i] = ps.getPosition();
            i++;
        }
        return Vector3f.listToBuffer(vs);
    }
    public void destroy(){
        particleobject.destroy();
    }
    @Override
     public void setPosition(Vector3f offset) {
        this.offset = offset;
    }
     public void setParticlesPerSecond(float p){
         pps = p;
     }
    @Override
     public Drawable getDrawable(){
         return particleobject;
     }

    @Override
    public void setParentInfo(Component parent) {
        if(parent instanceof Positioned){
            p = (Positioned) parent;
        }
    }

    @Override
    public void setRotation(Quaternionf rot) {
        this.rotoffset = rot;
    }

    @Override
    public Vector3f getPosition() {
        return offset;
    }

    @Override
    public Quaternionf getRotation() {
        return rotoffset;
    }

    @Override
    public void setScale(Vector3f v) {
        this.scale = v;
    }

    @Override
    public Vector3f getScale() {
        return scale;
    }
}
