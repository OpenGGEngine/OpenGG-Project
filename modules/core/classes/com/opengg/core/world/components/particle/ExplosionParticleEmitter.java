/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.particle;

import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.world.components.TriggerComponent;
import com.opengg.core.world.components.triggers.Trigger;
import com.opengg.core.world.components.triggers.TriggerInfo;
import com.opengg.core.world.components.triggers.Triggerable;

/**
 *
 * @author Javier
 */
public class ExplosionParticleEmitter extends ParticleEmitter implements Triggerable{
    private float velocity;
    private int amountPerExplosion = 10;

    public ExplosionParticleEmitter(float velocity, float life, Texture t) {
        super(t);
        this.velocity = velocity;
        this.setLifeLength(life);
    }
    
    public void setParticleAmountOnTrigger(int amount){
        this.amountPerExplosion = amount;
    }
    
    public void fire(int amount){
        for(int i = 0; i < amount; i++){
            float xd = (float) (Math.random() - 0.5f) * 2f;
            float yd = (float) (Math.random() - 0.5f) * 2f;
            float zd = (float) (Math.random() - 0.5f) * 2f;
            Vector3f finalv = new Vector3f(xd,yd,zd).normalize();
            finalv = finalv.multiply(velocity);
            addParticle(new Particle(getPosition(), finalv, getLifeLength(), 1f));
        }
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public int getAmount() {
        return amountPerExplosion;
    }

    public void setAmount(int amountPer) {
        this.amountPerExplosion = amountPer;
    }
    
    @Override
    public void onTrigger(Trigger source, TriggerInfo info) {
        fire(amountPerExplosion);
    }
}
