/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.Vector3f;
import com.opengg.core.render.particle.ParticleType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Warren
 */
public class ParticleRenderComponent implements Updatable, Renderable{
    List<ParticleType> particles = new ArrayList<>();
    Component c;
    @Override
    public void update(float delta) {
        particles.stream().forEach((p) -> {
            p.update(delta);
        });
    }

    

    @Override
    public void render() {
        
    }

    @Override
    public void setParentInfo(Component parent) {
        c = parent;
    }

    @Override
    public void setPosition(Vector3f pos) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRotation(Vector3f rot) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector3f getPosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector3f getRotation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
