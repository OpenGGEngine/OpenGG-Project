/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.light;

import com.opengg.core.math.Vector3f;
import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Javier
 */
public class Light {
    public Vector3f pos = new Vector3f();
    public Vector3f color = new Vector3f();
    public float distance = 10;
    public float distance2 = 50;
    public boolean moved = false;
    public static int bfsize = 12;
    
    public Light(Vector3f pos, Vector3f color, float distance, float distance2){
        this.pos = pos;
        this.color = color;
        this.distance = distance;
        this.distance2 = distance2;
    }
    
    public FloatBuffer getBuffer(){
        FloatBuffer fb = MemoryUtil.memAllocFloat(bfsize);
        fb.put(pos.x).put(pos.y).put(pos.z);
        fb.put(0);
        fb.put(color.x).put(color.y).put(color.z);
        fb.put(distance);
        fb.put(distance2);
        fb.put(0);
        fb.put(0);
        fb.put(0);
        fb.flip();
        return fb;
    }
}
