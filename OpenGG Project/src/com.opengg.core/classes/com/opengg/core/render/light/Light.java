/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.light;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.system.Allocator;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;

/**
 *
 * @author Javier
 */
public class Light {
    public static final int BUFFERSIZE = 48;

    private Vector3f pos = new Vector3f();
    private Quaternionf rot = new Quaternionf();
    private Vector3f color = new Vector3f();
    private float distance = 10;
    private float distance2 = 50;

    private boolean shadow;
    private Matrix4f perspective;
    private Framebuffer lightbuffer;

    private boolean changed = false;
    private boolean isActive = true;
  
    public Light(Vector3f pos, Vector3f color, float distance, float distance2){
        this(pos, new Quaternionf(), color, distance, distance2, new Matrix4f(), 0, 0, false);
    }

    public Light(Vector3f pos, Quaternionf rot, Vector3f color, float distance, float distance2, Matrix4f perspective, int xres, int yres){
        this(pos, rot, color, distance, distance2, perspective, xres, yres, true);
    }
    
    public Light(Vector3f pos, Quaternionf rot, Vector3f color, float distance, float distance2, Matrix4f perspective, int xres, int yres, boolean shadow){
        this.pos = pos;
        this.rot = rot;
        this.color = color;
        this.distance = distance;
        this.distance2 = distance2;
        this.shadow = shadow;
        this.perspective = perspective;

        if(!shadow) return;
        lightbuffer = Framebuffer.generateFramebuffer();
        lightbuffer.attachRenderbuffer(xres, yres, GL_RGBA8, GL_COLOR_ATTACHMENT0);
        lightbuffer.attachDepthTexture(xres, yres);
    }

    public FloatBuffer getBuffer(){
        FloatBuffer fb = Allocator.allocFloat(BUFFERSIZE);
        fb.put(pos.x).put(pos.y).put(pos.z);
        fb.put(1f);
        fb.put(color.x).put(color.y).put(color.z);
        fb.put(1f);
        fb.put(new Matrix4f().translate(pos).getTransposedBuffer());
        fb.put(perspective.getBuffer());

        fb.put(distance);
        fb.put(distance2);
        fb.put(shadow ? 1 : 0);
        fb.put(new float[]{0,0,0,0,0});
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
    
    public Matrix4f getPerspective(){
        return perspective;
    }

    public Framebuffer getLightbuffer() {
        return lightbuffer;
    }

    public void setLightbuffer(Framebuffer lightbuffer) {
        this.lightbuffer = lightbuffer;
    }

    public float getDistance() {
        return distance;
    }

    public Matrix4f getView(){
        return new Matrix4f().translate(pos);//rotate(rot).translate(pos);
    }

    public boolean isActive(){
        return isActive;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setRotation(Quaternionf rot){
        this.rot = rot;
    }

    public void setPerspective(Matrix4f perspective){
        this.perspective = perspective;
    }
}
