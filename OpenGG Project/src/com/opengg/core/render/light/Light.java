/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.light;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.Framebuffer;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Javier
 */
public class Light {
    Vector3f pos = new Vector3f();
    Vector3f color = new Vector3f();
    float distance = 10;
    float distance2 = 50;
    public boolean changed = false;
    public static int bfsize = 48;
    boolean shadow;
    Matrix4f view;
    Matrix4f perspective;
    Framebuffer lightbuffer;
  
    public Light(Vector3f pos, Vector3f color, float distance, float distance2){
        this.pos = pos;
        this.color = color;
        this.distance = distance;
        this.distance2 = distance2;
        shadow = false;
        view = new Matrix4f();
        perspective = new Matrix4f();
    }
    
    public Light(Vector3f pos, Vector3f color, float distance, float distance2, Matrix4f view, Matrix4f perspective, int xres, int yres){
        this.pos = pos;
        this.color = color;
        this.distance = distance;
        this.distance2 = distance2;
        shadow = true;
        this.view = view;
        this.perspective = perspective;
        
        lightbuffer = Framebuffer.generateFramebuffer();
        lightbuffer.attachRenderbuffer(xres, yres, GL_RGBA8, GL_COLOR_ATTACHMENT0);
    }

    public FloatBuffer getBuffer(){
        FloatBuffer fb = MemoryUtil.memAllocFloat(bfsize);
        fb.put(pos.x).put(pos.y).put(pos.z);
        fb.put(0);
        fb.put(color.x).put(color.y).put(color.z);
        fb.put(distance);
        fb.put(distance2);
        fb.put(shadow ? 1 : 0);
        fb.put(view.getBuffer());
        fb.put(perspective.getBuffer());
        fb.put(new float[]{0,0,0,0,0,0});
        fb.flip();
        return fb;
    }
    
    public Vector3f getPosition() {
        return pos;
    }

    public void setPosition(Vector3f pos) {
        this.pos = pos;
        changed = true;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
        changed = true;
    }
    
    public boolean hasShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public Matrix4f getView() {
        return view;
    }

    public void setView(Matrix4f view) {
        this.view = view;
    }

    public Matrix4f getPerspective() {
        return perspective;
    }

    public void setPerspective(Matrix4f perspective) {
        this.perspective = perspective;
    }
    
    public Framebuffer getLightbuffer() {
        return lightbuffer;
    }

    public void setLightbuffer(Framebuffer lightbuffer) {
        this.lightbuffer = lightbuffer;
    }
}
