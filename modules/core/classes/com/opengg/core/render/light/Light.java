/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.light;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.shader.CommonUniforms;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Framebuffer;
import com.opengg.core.system.Allocator;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;

/**
 *
 * @author Javier
 */
public class Light {
    public static final int BUFFERSIZE = 48;
    public static final float
            POINT = 1,
            ORTHO = 2,
            PERSPECTIVE = 3,
            NONE = 0;

    private Vector3f pos = new Vector3f();
    private Quaternionf rot = new Quaternionf();
    private Vector3f color = new Vector3f(1,1,1);
    private float distance = 10;
    private float angle = 20;
    private float type = NONE;

    private boolean shadow = false;
    private Matrix4f perspective = new Matrix4f();
    private Framebuffer lightBuffer;
    private int xres, yres;

    private boolean changed = false;
    private boolean isActive = true;

    public static Light createPoint(Vector3f pos, Vector3f color, float distance){
        return new Light(pos, new Quaternionf(), color, distance, 360, POINT);
    }

    public static Light createPointShadow(Vector3f pos, Vector3f color, float distance, int xres, int yres){
        Light light = new Light(pos, new Quaternionf(), color, distance, 360, POINT);
        light.createCubemap(xres, yres);
        return light;
    }

    public static Light createDirectional(Quaternionf rot, Vector3f color){
        return new Light(new Vector3f(), rot, color, 100000f, 360, ORTHO);
    }

    public static Light createDirectionalShadow(Quaternionf rot, Vector3f color, Vector3f pos, Matrix4f perspective, int xres, int yres){
        Light light = new Light(pos, rot, color, -1, 360, ORTHO);
        light.create2DMap(perspective, xres, yres);
        return light;
    }

    public static Light createFromStream(GGInputStream in) throws IOException {
        Light light = new Light();
        light.deserialize(in);
        switch ((int) light.type){
            case 1 -> light.createCubemap(light.xres,light.yres);
            case 2 -> light.create2DMap(light.perspective,light.xres,light.yres);
        }
        return light;
    }

    private Light() {

    }


    private Light(Vector3f pos, Quaternionf rot, Vector3f color, float distance, float angle, float type){
        this.pos = pos;
        this.rot = rot;
        this.color = color;
        this.distance = distance;
        this.angle = angle;
        this.type = type;
    }

    private void create2DMap(Matrix4f perspective, int xres, int yres){
        this.perspective = perspective;
        lightBuffer = Framebuffer.generateFramebuffer();
        lightBuffer.attachRenderbuffer(xres, yres, GL_RGBA, GL_COLOR_ATTACHMENT0);
        lightBuffer.attachDepthTexture(xres, yres);
        this.xres = xres;
        this.yres = yres;
        shadow = true;
        type = ORTHO;
    }

    private void createCubemap(int xres, int yres){
        this.perspective = Matrix4f.perspective(90f, (float)xres/(float)yres, 0.1f, distance);
        lightBuffer = Framebuffer.generateFramebuffer();
        lightBuffer.attachColorCubemap(xres, yres, 0);
        lightBuffer.attachDepthCubemap(xres, yres);
        this.xres = xres;
        this.yres = yres;
        shadow = true;
        type = POINT;
    }

    public FloatBuffer getBuffer(){
        FloatBuffer fb = Allocator.stackAllocFloat(BUFFERSIZE);
        fb.put(pos.x).put(pos.y).put(pos.z);
        fb.put(1f);
        fb.put(color.x).put(color.y).put(color.z);
        fb.put(1f);
        fb.put(rot.transform(new Vector3f(0,0,-1)).getStackBuffer());
        Allocator.popStack();
        fb.put(0);

        fb.put(new Matrix4f().translate(pos).rotate(rot).invert().getStackBuffer());
        Allocator.popStack();
        fb.put(perspective.getStackBuffer());
        Allocator.popStack();

        fb.put(distance);
        fb.put(type);
        fb.put(shadow ? 1 : 0);
        fb.put(angle);
        fb.flip();
        return fb;
    }

    public void initializeRender(){
        if(!shadow) throw new IllegalStateException("No shadowmap has been created for this light");

        CommonUniforms.setView(getView());
        CommonUniforms.setProjection(getPerspective());
        if(type == ORTHO){
            ShaderController.useConfiguration("passthrough");
        }else{
            ShaderController.useConfiguration("pointshadow");

            var matrix4fs = new Matrix4f[6];
            matrix4fs[0] = getPerspective().multiply(
                    Matrix4f.lookAt(pos, pos.add(new Vector3f( 1, 0, 0)), new Vector3f(0,-1, 0)));
            matrix4fs[1] = getPerspective().multiply(
                    Matrix4f.lookAt(pos, pos.add(new Vector3f(-1, 0, 0)), new Vector3f(0,-1, 0)));
            matrix4fs[2] = getPerspective().multiply(
                    Matrix4f.lookAt(pos, pos.add(new Vector3f( 0, 1, 0)), new Vector3f(0, 0, 1)));
            matrix4fs[3] = getPerspective().multiply(
                    Matrix4f.lookAt(pos, pos.add(new Vector3f( 0,-1, 0)), new Vector3f(0, 0,-1)));
            matrix4fs[4] = getPerspective().multiply(
                    Matrix4f.lookAt(pos, pos.add(new Vector3f( 0, 0, 1)), new Vector3f(0,-1, 0)));
            matrix4fs[5] = getPerspective().multiply(
                    Matrix4f.lookAt(pos, pos.add(new Vector3f( 0, 0,-1)), new Vector3f(0,-1, 0)));

            ShaderController.setUniform("shadowMatrices", matrix4fs);
            ShaderController.setUniform("lightPos", pos);
            ShaderController.setUniform("farplane", distance);
        }
        lightBuffer.clearFramebuffer(new Vector3f());
        lightBuffer.enableRendering(0,0,lightBuffer.getWidth(), lightBuffer.getHeight());
    }

    public void finalizeRender(int pos){
        lightBuffer.disableRendering();
        if(this.type == POINT)
            lightBuffer.getTexture(Framebuffer.DEPTH).setAsUniform("shadowcube");
        else
            lightBuffer.getTexture(Framebuffer.DEPTH).setAsUniform("shadowmap" + pos);
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

    public Framebuffer getLightBuffer() {
        return lightBuffer;
    }

    public void setLightBuffer(Framebuffer lightBuffer) {
        this.lightBuffer = lightBuffer;
    }

    public float getDistance() {
        return distance;
    }

    public Matrix4f getView(){
        return new Matrix4f().translate(pos).rotate(rot).invert();
    }

    public void setActive(boolean active){
        this.isActive = active;
    }

    public boolean isActive(){
        return isActive;
    }

    public void setDistance(float distance) {
        this.distance = distance;
        if(this.type == POINT && this.hasShadow()){
            setPerspective(Matrix4f.perspective(90f, (float)xres/(float)yres, 0.1f, distance));
        }
    }

    public void setRotation(Quaternionf rot){
        this.rot = rot;
    }

    public void setPerspective(Matrix4f perspective){
        this.perspective = perspective;
    }

    public float getType() {
        return type;
    }

    public void serialize(GGOutputStream out) throws IOException {
        out.write(pos);
        out.write(rot);
        out.write(color);
        out.write(distance);
        out.write(angle);
        out.write(type);
        out.write(shadow);
        out.write(perspective);
        out.write(xres);
        out.write(yres);
    }

    private void deserialize(GGInputStream in) throws IOException {
        pos = in.readVector3f();
        rot = in.readQuaternionf();
        color = in.readVector3f();
        distance = in.readFloat();
        angle = in.readFloat();
        type = in.readFloat();
        shadow = in.readBoolean();
        perspective = in.readMatrix4f();
        xres = in.readInt();
        yres = in.readInt();
    }
}
