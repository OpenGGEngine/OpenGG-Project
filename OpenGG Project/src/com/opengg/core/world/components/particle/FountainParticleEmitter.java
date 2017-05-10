/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.particle;

import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.InstancedDrawnObject;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;

/**
 *
 * @author Javier
 */
public class FountainParticleEmitter extends ParticleEmitter{
    private float pps;
    private float speed;
    private float gravityComplient;
    private float lifeLength;
    private float timeSinceLast = 0f;
    
    public FountainParticleEmitter(float pps, float speed, float lifeLength, Texture t) {
        super((InstancedDrawnObject)ObjectCreator.createInstancedQuadPrism(new Vector3f(0,0,0), new Vector3f(1,0,1)), t);
        ((InstancedDrawnObject)this.getDrawable()).setAdjacency(false);
        this.pps = pps;
        this.speed = speed;
        this.gravityComplient = -0.027f;
        this.lifeLength = lifeLength;
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
    public void update(float delta){
        super.update(delta);
        timeSinceLast += delta;
        if(timeSinceLast > 1f/pps)
            emitParticle(this.getPosition());
    }
}
