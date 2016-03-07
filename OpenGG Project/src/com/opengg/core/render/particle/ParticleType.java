/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.particle;

import com.opengg.core.Vector3f;
import com.opengg.core.components.Component;
import java.util.ArrayList;
import java.util.List;
import com.opengg.core.render.particle.Particle;
import java.util.LinkedList;

/**
 *
 * @author Warren
 */
public class ParticleType implements Component{
    List<Particle> particles = new LinkedList<>();
    private Vector3f offset = new Vector3f();
    private float pps;
    private float speed;
    private float gravityComplient;
    private float lifeLength;
    
    public ParticleType(float pps, float speed, float gravityComplient, float lifeLength) {
        this.pps = pps;
        this.speed = speed;
        this.gravityComplient = gravityComplient;
        this.lifeLength = lifeLength;
    }
    
    private void emitParticle(Vector3f center){
        float dirX = (float) Math.random() * 2f - 1f;
        float dirZ = (float) Math.random() * 2f - 1f;
        Vector3f velocity = new Vector3f(dirX, 1, dirZ);
        velocity.normalize();
        velocity.multiply(speed);
        particles.add(new Particle(new Vector3f(center), velocity, new Vector3f(0,gravityComplient,0), lifeLength,1f));
    }
    @Override
    public void update() {
        emitParticle(offset);
        particles.stream().forEach((p) -> {
            boolean stayingAlive = p.update();
            if(stayingAlive){
                particles.remove(p);
            }
        });
    }


    
}