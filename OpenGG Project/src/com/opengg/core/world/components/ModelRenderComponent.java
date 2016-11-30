/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector3f;
import com.opengg.core.model.Model;
import com.opengg.core.render.drawn.Drawable;

/**
 *
 * @author Warren
 * 
 * This Component Renders a Drawable
 */
public class ModelRenderComponent extends ComponentHolder implements Renderable{
    Drawable g;
    private Vector3f offset = new Vector3f(0,0,0);
    private Vector3f rotationoffset = new Vector3f(0,0,0);
    Positioned parent;
   
    public ModelRenderComponent(Drawable g){
        this.g = g;
    }
    
    public ModelRenderComponent(Model m){
        g = m.getDrawable();
    }

    @Override
    public void render() {
        Matrix4f m = Matrix4f.translate(getPosition());
        
        g.setMatrix(m);
        g.draw();
    }

    @Override
    public void setPosition(Vector3f pos) {
        offset = pos;
    }

    @Override
    public void setRotation(Vector3f rot) {
        rotationoffset = rot;
    }

    @Override
    public void setParentInfo(Component parent) {
        if(parent instanceof Positioned){
            this.parent = (Positioned) parent;
        }
    }

    @Override
    public Vector3f getPosition() {
        return new Vector3f(parent.getPosition().x + offset.x, parent.getPosition().y + offset.y, parent.getPosition().z + offset.z);
    }

    @Override
    public Vector3f getRotation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Drawable getDrawable() {
        return g;
    }

    
    
}
