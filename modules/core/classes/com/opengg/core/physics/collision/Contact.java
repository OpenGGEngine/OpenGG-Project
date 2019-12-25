/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision;

import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class Contact {
    public Vector3f offset1;
    public Vector3f offset2;
    public Vector3f normal;
    public float depth;
    public long timestamp = 0;

    public Contact() {
    }

    public Contact(Vector3f offset1, Vector3f offset2, Vector3f normal, float depth) {
        this.offset1 = offset1;
        this.offset2 = offset2;
        this.normal = normal;
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "position=" + offset1 +
                ", normal=" + normal +
                ", depth=" + depth +
                '}';
    }
}








