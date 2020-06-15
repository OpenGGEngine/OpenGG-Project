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
    public Vector3f vec;
    
    public MinkowskiSet(){
        this.a = new Vector3f();
        this.b = new Vector3f();
        this.vec = new Vector3f();
    }
    
    public MinkowskiSet(Vector3f a, Vector3f b, Vector3f vec){
        this.a = a;
        this.b = b;
        this.vec = vec;
    }

    @Override
    public String toString() {
        return "MinkowskiSet{" +
                "a=" + a +
                ", b=" + b +
                ", v=" + vec +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinkowskiSet that = (MinkowskiSet) o;

        if (!a.equals(that.a)) return false;
        return b.equals(that.b);
    }

    @Override
    public int hashCode() {
        int result = a.hashCode();
        result = 31 * result + b.hashCode();
        return result;
    }
}
