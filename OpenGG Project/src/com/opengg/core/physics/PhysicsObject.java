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
    public Vector3f position = new Vector3f();
    public Quaternionf rotation = new Quaternionf();
    public Vector3f scale = new Vector3f();

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
    
    public PhysicsSystem getSystem(){
        return system;
    }
}
