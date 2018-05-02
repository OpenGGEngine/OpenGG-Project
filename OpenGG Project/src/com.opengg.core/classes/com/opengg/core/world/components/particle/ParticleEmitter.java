/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.particle;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.system.Allocator;
import com.opengg.core.world.components.RenderComponent;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Warren
 * 
 * This represents a group of particles
 */
public abstract class ParticleEmitter extends RenderComponent{
    List<Particle> particles = new LinkedList<>();
    Texture t;
    float gravityComplient;
    float lifeLength;
    
    public ParticleEmitter(Texture t, float lifelength){
        createDrawable();
        this.setFormat(RenderEngine.getParticleFormat());
        this.setShader("particle");
        this.t = t;
        this.lifeLength = lifelength;
    }
    
    private void createDrawable(){
        Buffer[] buffers = ObjectCreator.createSquareBuffers(new Vector2f(-0.5f,-0.5f), new Vector2f(0.5f,0.5f), 0);
        FloatBuffer fb = (FloatBuffer) buffers[0];
        IntBuffer ib = (IntBuffer) buffers[1];
        this.setDrawable(new DrawnObject(ib, fb, Allocator.allocFloat(3)));
    }
    
    private FloatBuffer createParticleVBO(){
        Vector3f[] vs = new Vector3f[particles.size()];
        int i = 0;
        for(Particle ps : particles){
            vs[i] = ps.getPosition();
            i ++;
        }
        return Vector3f.listToBuffer(vs);
    }
    
    public void addParticle(Particle p){
        particles.add(p);
    }

    public Texture getTexture() {
        return t;
    }

    public void setTexture(Texture t) {
        this.t = t;
    }

    public float getLifeLength() {
        return lifeLength;
    }

    public void setLifeLength(float lifeLength) {
        this.lifeLength = lifeLength;
    }
    
    
    
    @Override
    public void update(float delta) {
        Iterator<Particle> screwconcurrent = particles.iterator();
        while(screwconcurrent.hasNext()){
            Particle p2 = screwconcurrent.next();
            if(p2.update(delta)){
                screwconcurrent.remove();             
            }
        }
    }
    
    @Override
    public void render(){
        ((DrawnObject)getDrawable()).updateBuffer(1, createParticleVBO());
        t.use(0);
        super.render();
    }
}
