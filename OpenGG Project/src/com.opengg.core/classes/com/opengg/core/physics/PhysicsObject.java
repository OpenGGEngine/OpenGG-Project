/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class PhysicsObject {
    public PhysicsSystem system;
    public PhysicsObject parent;
    public Vector3f position = new Vector3f();
    public Quaternionf rotation = new Quaternionf();
    public Vector3f scale = new Vector3f(1,1,1);

    public Vector3f getPosition() {
        if(parent != null)
            return parent.getPosition().add(parent.getRotation().transform(position).multiply(parent.getScale()));
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Quaternionf getRotation() {
        if(parent != null)
            return rotation.multiply(parent.getRotation());
        return rotation;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        if(parent != null)
            return scale.multiply(parent.getScale());
        
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
    
    public PhysicsSystem getSystem(){
        return system;
    }

}
