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
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.system.Allocator;
import com.opengg.core.world.components.RenderComponent;

import java.awt.*;
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

    public ParticleEmitter() {
        this(Texture.create(Texture.config(),TextureManager.getDefault()));
    }

    public ParticleEmitter(Texture t){
        createDrawable();
        this.setFormat(RenderEngine.getParticleFormat());
        this.setShader("particle");
        this.t = t;
    }
    
    private void createDrawable(){
        var buffers = ObjectCreator.createSquareBuffers(new Vector2f(-0.05f,-0.05f), new Vector2f(0.05f,0.05f), 0);
        FloatBuffer fb = buffers.x;
        IntBuffer ib = buffers.y;
        this.setDrawable(new DrawnObject(RenderEngine.getParticleFormat(), ib, fb, Allocator.allocFloat(3)));
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

    public List<Particle> getParticles(){
        return particles;
    }

    @Override
    public void update(float delta) {
        particles.removeIf(p2 -> p2.update(delta));
    }
    
    @Override
    public void render(){
        ((DrawnObject)getDrawable()).updateBuffer(1, createParticleVBO());
        ((DrawnObject)getDrawable()).setInstanceCount(particles.size());
        //t.use(0);
        super.render();
    }
}
