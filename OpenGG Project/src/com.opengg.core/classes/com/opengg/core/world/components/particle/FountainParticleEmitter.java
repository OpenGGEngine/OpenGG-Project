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
public class FountainParticleEmitter extends ParticleEmitter{
    float pps;
    float speed;
    float timeSinceLast = 0f;
    float deviation;
    Vector3f angle;
    boolean paused;
    
    public FountainParticleEmitter(float pps, float speed, float lifeLength, float deviation, Vector3f angle, Texture t) {
        super(t, lifeLength);
        this.pps = pps;
        this.speed = speed;
        this.lifeLength = lifeLength;
        this.deviation = deviation;
        this.angle = angle.normalize();
    }
    
    private void emitParticle(){
        float offx = (float) Math.random() * deviation * 2 - deviation;
        float offy = (float) Math.random() * deviation * 2 - deviation;
        float offz = (float) Math.random() * deviation * 2 - deviation;
        
        Quaternionf offset = new Quaternionf(new Vector3f(offx, offy, offz));
        
        Vector3f finalv = offset.transform(angle);
        finalv = getRotation().transform(finalv);
        finalv = finalv.multiply(speed);
        
        addParticle(new Particle(getPosition(), finalv, new Vector3f(0,gravityComplient,0), lifeLength,1f));
    }
    
    @Override
    public void update(float delta){
        super.update(delta);
        timeSinceLast += delta;
        while(!(timeSinceLast < 1f/pps)){
            if(!paused)
                emitParticle();
            timeSinceLast -= 1f/pps;
        }
    }
}