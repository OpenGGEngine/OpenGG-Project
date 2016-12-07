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
    public AudioListener(){}
    public Vector3f pos = new Vector3f();
    public Vector3f rot = new Vector3f();
    public Vector3f vel = new Vector3f();

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public Vector3f getRot() {
        return rot;
    }

    public void setRot(Vector3f rot) {
        this.rot = rot;
    }

    public Vector3f getVel() {
        return vel;
    }

    public void setVel(Vector3f vel) {
        this.vel = vel;
    }
    
}
