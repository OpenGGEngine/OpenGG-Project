/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.physics;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObjectGroup;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.Renderable;

/**
 *
 * @author Javier
 */
public class ColliderRenderer implements Renderable {
    Drawable drawn;
    Collider collider;
    
    public ColliderRenderer(Collider collider){
        this.collider = collider;
        DrawnObjectGroup d = new DrawnObjectGroup();
        collider.boxes.stream().forEach((c) -> {
            Vector3f[] vertices = c.getAABBVertices();
            d.add(ObjectCreator.createQuadPrism(vertices[0], vertices[1]));
        });
        drawn = d;
    }
    
    
    @Override
    public void render() {
        Matrix4f posm = new Matrix4f().translate(collider.getPosition());
        drawn.setMatrix(posm);
        drawn.draw();
    }

    @Override
    public Drawable getDrawable() {
        return drawn;
    }

    @Override
    public void setPosition(Vector3f pos) {
    }

    @Override
    public void setRotation(Quaternionf rot) {
        
    }

    @Override
    public Vector3f getPosition() {
        return collider.getPosition();
    }

    @Override
    public void setParentInfo(Component parent) {}

    @Override
    public Quaternionf getRotation() {
        return collider.getRotation();
    }

    @Override
    public void setScale(Vector3f v) {
       
    }

    @Override
    public Vector3f getScale() {
        return new Vector3f();
    }
    
}
