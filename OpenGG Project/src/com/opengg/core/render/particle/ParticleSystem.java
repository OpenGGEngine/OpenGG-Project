/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.particle;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector3f;
import com.opengg.core.render.drawn.InstancedDrawnObject;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Warren
 * 
 * This represents a group of particles
 */
public class ParticleSystem{
    List<Particle> particles = new LinkedList<>();
    private Vector3f position = new Vector3f();
    InstancedDrawnObject particleobject;
    private float pps;
    private float speed;
    private float gravityComplient;
    private float lifeLength;
    
    public ParticleSystem(float pps, float speed, float gravityComplient, float lifeLength) {
        this.pps = pps;
        this.speed = speed;
        this.gravityComplient = gravityComplient;
        this.lifeLength = lifeLength;
         FloatBuffer f = BufferUtils.createFloatBuffer(48);
            f.put(0);
            f.put(0);
            f.put(0); 
            f.put(1);
            f.put(1);
            f.put(1);
            f.put(1);
            f.put(1);
            
            f.put(0);
            f.put(100);
            f.put(0);
            f.put(1);
            f.put(1);
            f.put(1);
            f.put(1);
            f.put(1);
            
            f.put(100);
            f.put(100);
            f.put(0);
            f.put(1);
            f.put(1);
            f.put(1);
            f.put(1);
            f.put(1);
            
            f.put(100);
            f.put(100);
            f.put(0);     
            f.put(1);
            f.put(1);
            f.put(1);
            f.put(1);
            f.put(1);
            
            f.put(0);
            f.put(0);
            f.put(0);
            f.put(1);
            f.put(1);
            f.put(1);
            f.put(1);
            f.put(1);
            
            f.put(100);
            f.put(0);
            f.put(0);  
            f.put(1);
            f.put(1);
            f.put(1);
            f.put(1);
            f.put(1);
            particleobject = new InstancedDrawnObject(f,2,createParticleVBO());
        
    }
    public ParticleSystem(float pps, float speed, float gravityComplient, float lifeLength,FloatBuffer model) {
        this.pps = pps;
        this.speed = speed;
        this.gravityComplient = gravityComplient;
        this.lifeLength = lifeLength;
        particleobject = new InstancedDrawnObject(model,2,createParticleVBO());
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
        emitParticle(position);
        
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
        //particleobject.setMatrix(Matrix4f.translate(position));
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
}
