/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.io.objloader.parser.MTLMaterial;
import com.opengg.core.render.texture.Texture;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Warren
 */
public class TexturedDrawnObject implements Drawable{
    Texture normalmap;
    Texture specularmap;
    MTLMaterial m = new MTLMaterial();
    Texture tex;
    DrawnObject object;
     public TexturedDrawnObject(FloatBuffer b,int vertSize){
        object = new DrawnObject(b,vertSize);
    }
    public TexturedDrawnObject(FloatBuffer b,int vertSize,Texture t){
        object = new DrawnObject(b,vertSize);
        this.tex = t;
    }
    public TexturedDrawnObject(FloatBuffer b,IntBuffer i,Texture t){
        object = new DrawnObject(b,i);
        this.tex = t;
    }
    @Override
    public void draw() {
        tex.useTexture(0);
        object.draw();
    }

    @Override
    public void setMatrix(Matrix4f m) {
        object.setMatrix(m);
    }

    @Override
    public Matrix4f getMatrix() {
       return object.model;
    }
    
    @Override
    public void destroy() {
        object.destroy();
    }
    //Setters
    public void setNormalMap(Texture normalmap) {
        this.normalmap = normalmap;
    }

    public void setSpecularMap(Texture specularmap) {
        this.specularmap = specularmap;
    }

    public void setMaterial(MTLMaterial m) {
        this.m = m;
    }

    public void setTexture(Texture tex) {
        this.tex = tex;
    }
}
