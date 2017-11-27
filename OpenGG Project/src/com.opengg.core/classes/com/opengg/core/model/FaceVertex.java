/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector4f;

/**
 *
 * @author Warren
 */
public class FaceVertex {
    int index = -1;
    public Vector3f v = new Vector3f();
    public Vector2f t = new Vector2f();
    public Vector3f n = new Vector3f();
    public Vector4f j = new Vector4f();
    public Vector4f w = new Vector4f();
    

    @Override
    public boolean equals(Object eq){
        if(eq instanceof FaceVertex){
            FaceVertex e = (FaceVertex) eq;
            if(!this.v.equals(e.v))
                return false;
            if(!this.t.equals(e.t))
                return false;
            if(!this.n.equals(e.n))
                return false;
            if(!this.j.equals(e.j))
                return false;
            if(!this.w.equals(e.w))
                return false;
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return v.toString();
    }
}
