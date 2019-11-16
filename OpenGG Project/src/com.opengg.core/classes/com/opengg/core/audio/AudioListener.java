/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class AudioListener {
    private Vector3f position = new Vector3f();
    private Vector3f rotation = new Vector3f();
    private Vector3f velocity = new Vector3f();

    public AudioListener(){}


    public AudioListener(Vector3f position, Vector3f rotation, Vector3f velocity){
        this.position = position;
        this.rotation = rotation;
        this.velocity = velocity;
    }
    
    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }
    
}
