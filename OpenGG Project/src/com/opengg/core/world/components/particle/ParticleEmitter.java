/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.particle;

import com.opengg.core.engine.RenderEngine;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.InstancedDrawnObject;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.world.components.RenderComponent;
import java.nio.FloatBuffer;
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
    
    public ParticleEmitter(InstancedDrawnObject drawn, Texture t){
        this.setDrawable(drawn);
        this.setFormat(RenderEngine.getParticleFormat());
        this.setShader("particle");
        this.t = t;
    }
    
    private FloatBuffer createParticleVBO(){
        Vector3f[] vs = new Vector3f[particles.size()];
        int i = 0;
        for(Particle ps : particles){
            vs[i] = ps.getPosition();
            i++;
        }
        return Vector3f.listToBuffer(vs);
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
        ((InstancedDrawnObject)getDrawable()).setPositions(createParticleVBO(),particles.size());
    }
    
    @Override
    public void render(){
        t.use(0);
        super.render();
    }
}
