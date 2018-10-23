/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics.collision;

import com.opengg.core.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ContactManifold {
    public Vector3f normal;
    public List<Vector3f> points = new ArrayList<>(3);
    public float depth;

    public ContactManifold(){}

    public ContactManifold(Vector3f normal, List<Vector3f> points, float depth) {
        this.normal = normal;
        this.points = points;
        this.depth = depth;
    }

    public ContactManifold reverse(){
        normal = normal.inverse();
        return this;
    }
    
    @Override
    public String toString(){
        String s = "";
        s += "Collision normal: " + normal + "\n";
        s += "Collision points: " + points + "\n";
        s += "Collision depth: " + depth + "\n";
        return s;
    }
}
