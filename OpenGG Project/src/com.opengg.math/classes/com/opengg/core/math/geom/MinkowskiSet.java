/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.math.geom;

import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public class MinkowskiSet {
    public Vector3f a;
    public Vector3f b;
    public Vector3f v;
    
    public MinkowskiSet(){
        this.a = new Vector3f();
        this.b = new Vector3f();
        this.v = new Vector3f();
    }
    
    public MinkowskiSet(Vector3f a, Vector3f b, Vector3f v){
        this.a = a;
        this.b = b;
        this.v = v;
    }

    @Override
    public String toString() {
        return "MinkowskiSet{" +
                "a=" + a +
                ", b=" + b +
                ", v=" + v +
                '}';
    }
}
