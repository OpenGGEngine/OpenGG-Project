/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.particle;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.Texture;

/**
 *
 * @author Javier
 */
public class DirectionalExplosionParticleEmitter extends ExplosionParticleEmitter{
    private final Vector3f direction;
    private final float deviation;
    
    public DirectionalExplosionParticleEmitter(float velocity, float life, Texture t) {
        this(velocity, life, new Vector3f(), 10, t);
    }
    
    public DirectionalExplosionParticleEmitter(float velocity, float life, Vector3f direction, float deviation, Texture t) {
        super(velocity, life, t);
        this.direction = direction;
        this.deviation = deviation;
    }
    
    @Override
    public void fire(int amount){
        for(int i = 0; i < amount; i++){
            float offx = (float) Math.random() * deviation * 2 - deviation;
            float offy = (float) Math.random() * deviation * 2 - deviation;
            float offz = (float) Math.random() * deviation * 2 - deviation;
        
            Quaternionf offset = Quaternionf.createXYZ(new Vector3f(offx, offy, offz));
        
            Vector3f finalv = offset.transform(direction);
            finalv = getRotation().transform(finalv);
            finalv = finalv.multiply(getVelocity());
            addParticle(new Particle(getPosition(), finalv, getLifeLength(), 1f));
        }
    }
}
