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
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.system.Allocator;
import com.opengg.core.world.components.RenderComponent;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Warren
 * 
 * This represents a group of particles
 */
public abstract class ParticleEmitter extends RenderComponent{
    private final List<Particle> particles = new LinkedList<>();
    private Texture texture;
    private boolean bindParticlesToEmitter = false;
    private float lifeLength = 1f;

    public ParticleEmitter() {
        this(Texture.create(Texture.config(), TextureManager.getDefault()));
    }

    public ParticleEmitter(Texture texture){
        createDrawable();
        this.setFormat(RenderEngine.getParticleFormat());
        this.setShader("particle");
        this.texture = texture;
    }

    private void createDrawable(){
        var buffers = ObjectCreator.createSquareBuffers(new Vector2f(-1,-1), new Vector2f(1,1), 0);
        FloatBuffer fb = buffers.vertices();
        IntBuffer ib = buffers.indices();
        this.setRenderable(DrawnObject.create(RenderEngine.getParticleFormat(), ib, fb, Allocator.allocFloat(3)));
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
        return texture;
    }

    public void setTexture(TextureData tex) {
        this.texture = Texture.create(Texture.config(), tex);
    }

    public float getLifeLength() {
        return lifeLength;
    }

    public void setLifeLength(float lifeLength) {
        this.lifeLength = lifeLength;
    }

    public boolean areParticlesBound() {
        return bindParticlesToEmitter;
    }

    public void setBindParticlesToEmitter(boolean bindParticlesToEmitter) {
        this.bindParticlesToEmitter = bindParticlesToEmitter;
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
        ((DrawnObject) getRenderable()).updateBuffer(1, createParticleVBO());
        ((DrawnObject) getRenderable()).setInstanceCount(particles.size());
        //if(!bindParticlesToEmitter) this.setOverrideMatrix(new Matrix4f().scale(this.getScale()));
        ShaderController.setUniform("Kd", texture);
        super.render();
    }
}
