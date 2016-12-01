/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.particle;

import com.opengg.core.Vector3f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.InstancedDrawnObject;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.util.Time;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.Renderable;
import com.opengg.core.world.components.Updatable;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Warren
 * 
 * This represents a group of particles
 */
public class ParticleSystem implements Updatable, Renderable{
    List<Particle> particles = new LinkedList<>();
    private Vector3f position = new Vector3f();
    InstancedDrawnObject particleobject;
    private float pps;
    private float speed;
    private float gravityComplient;
    private float lifeLength;
    private float timeSinceLast = 0f;
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
    
    private void emitParticle(Vector3f center){
        float dirX = (float) Math.random() * 2f - 1f;
        float dirZ = (float) Math.random() * 2f - 1f;
        Vector3f velocity = new Vector3f(dirX, 1, dirZ);
        velocity.normalize();
        velocity.multiply(speed);
        particles.add(new Particle(center, velocity, new Vector3f(0,gravityComplient,0), lifeLength,1f));
    }
    public void update(float delta) {
        timeSinceLast += time.getDeltaMs();
        if(timeSinceLast >= (1/pps)*1000){
            timeSinceLast = 0;
            emitParticle(position);
        }
        
        Iterator<Particle> screwconcurrent = particles.iterator();
        while(screwconcurrent.hasNext()){
            Particle p = screwconcurrent.next();
            if(p.update()){
                screwconcurrent.remove();             
            }
            
        }
        particleobject.setPositions(createParticleVBO(),particles.size());
    }
    public void render(){
        t.useTexture(0);
        particleobject.draw();
    }
    private FloatBuffer createParticleVBO(){
        FloatBuffer f = BufferUtils.createFloatBuffer(3* particles.size());
        for(Particle p:particles){ 
           f.put(p.getPosition().x );
           f.put(p.getPosition().y);
           f.put(p.getPosition().z);
        }
        f.flip();
        return f;
    }
    public void destroy(){
        particleobject.destroy();
    }
     public void setPosition(Vector3f position) {
        this.position = position;
    }
     public void setParticlesPerSecond(float p){
         pps = p;
     }
     public Drawable getDrawable(){
         return particleobject;
     }

    @Override
    public void setParentInfo(Component parent) {
        
    }

    @Override
    public void setRotation(Vector3f rot) {}

    @Override
    public Vector3f getPosition() {
        return position;
    }

    @Override
    public Vector3f getRotation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
