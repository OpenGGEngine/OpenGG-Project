/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.drawn;

import com.opengg.core.Matrix4f;
import com.opengg.core.io.objloader.parser.MTLMaterial;
import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.render.texture.Texture;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

/**
 *
 * @author Javier
 */
public class MatDrawnObject implements Drawable {
    DrawnObject d;
    MTLMaterial m;
    Texture tex = new Texture();
    private Texture normalmap;
    private boolean hasNormalMap;
    
    public MatDrawnObject(FloatBuffer b, int vertsize){
        d = new DrawnObject(b,vertsize);
    }
    
    public MatDrawnObject(List<FloatBuffer> buffers, VertexBufferObject vbo2, int vertSize){
        d = new DrawnObject(buffers,vbo2,vertSize);
    }
    
    public MatDrawnObject(FloatBuffer b, VertexBufferObject vbo2, IntBuffer index){
        d = new DrawnObject(b,vbo2,index);
    }
    
    public void setMaterial(MTLMaterial m){
        this.m = m;
    }
    public void setTexture(Texture d){
        this.tex = d;
    }
    public void setNormalMap(Texture d){
        this.normalmap = d;
        this.hasNormalMap = true;
        
    }
    public void setSpecularMap(Texture d){
        this.normalmap = d;
        this.hasNormalMap = true;   
    }
    public void setShaderMatrix(Matrix4f m){
        d.setShaderMatrix(m);
    }
    @Override
    public void draw() {
        tex.useTexture(0);
        d.draw();
    }

    @Override
    public void drawPoints() {
        d.drawPoints();
    }

    @Override
    public void saveShadowMVP() {
       d.saveShadowMVP();
    }

    @Override
    public void drawShaded() {
        tex.useTexture(0);
        d.drawShaded();
    }

    @Override
    public void setMatrix(Matrix4f m) {
        d.setMatrix(m);
    }

    @Override
    public Matrix4f getMatrix() {
        return d.getMatrix();
    }

    @Override
    public void destroy() {
        d.destroy();
    }
    
}
