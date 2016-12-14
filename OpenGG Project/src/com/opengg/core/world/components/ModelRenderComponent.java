/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
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
    private Vector3f offset = new Vector3f();
    private Vector3f scale = new Vector3f(1,1,1);
    private Quaternionf rotoffset = new Quaternionf();
    Positioned parent;
   
    public ModelRenderComponent(Drawable g){
        this.g = g;
    }
    
    public ModelRenderComponent(Model m){
        g = m.getDrawable();
    }

    @Override
    public void render() {
        Matrix4f m = new Matrix4f().translate(getPosition()).scale(getScale());
        g.setMatrix(m);
        g.draw();
    }

    @Override
    public void setPosition(Vector3f pos) {
        offset = pos;
    }

    @Override
    public void setRotation(Quaternionf rot) {
        rotoffset = rot;
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
    public Quaternionf getRotation() {
        //return new Quaternionf(rotoffset.multiply(parent.getRotation()));
        return parent.getRotation();
    }

    @Override
    public Drawable getDrawable() {
        return g;
    }

    @Override
    public void setScale(Vector3f v) {
        this.scale = v;
    }

    @Override
    public Vector3f getScale() {
        return scale;
    }

    
    
}
