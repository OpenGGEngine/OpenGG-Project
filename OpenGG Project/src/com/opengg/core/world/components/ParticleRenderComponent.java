/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.Vector3f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.particle.ParticleSystem;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Warren
 */
public class ParticleRenderComponent implements Updatable, Renderable{
    List<ParticleSystem> particles = new ArrayList<>();
    private Vector3f offset = new Vector3f(0,0,0);
    Positioned w;
    Component c;
    @Override
    public void update(float delta) {
        
        particles.stream().forEach((p) -> {
            p.setPosition(offset.add(w.getPosition()));
            p.update(delta);
        });
    }
    @Override
    public void render() {
        particles.stream().forEach((p) -> {
            p.render();
        });
    }

    @Override
    public void setParentInfo(Component parent) {
        if(parent instanceof Positioned){
            w = (Positioned) parent;
        }
    }

    @Override
    public void setPosition(Vector3f pos) {
       offset = pos;
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
    public void addParticleType(ParticleSystem t){
        particles.add(t);
    }

    @Override
    public Drawable getDrawable() {
        return particles.get(0).getDrawable();
    }
    
}
