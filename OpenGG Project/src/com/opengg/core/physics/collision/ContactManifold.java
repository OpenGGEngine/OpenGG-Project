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
public class ContactManifold {
    public Vector3f normal;
    public Vector3f point;
    public float depth;
    
    public ContactManifold reverse(){
        normal = normal.inverse();
        return this;
    }
    
    @Override
    public String toString(){
        String s = "";
        s += "Collision normal: " + normal + "\n";
        s += "Collision point: " + point + "\n";
        s += "Collision depth: " + depth + "\n";
        return s;
    }
}
