/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.drawn;

import com.opengg.core.Matrix4f;
import com.opengg.core.io.newobjloader.Material;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.util.GlobalInfo;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

/**
 *
 * @author Javier
 */
public class MatDrawnObject implements Drawable {
    DrawnObject d;

    public void setM(Material m) {
        this.m = m;
        d.hasmat = true;
    }
    Material m = Material.defaultmaterial;
    Texture tex;
    private Texture normalmap;
    private boolean hasNormalMap = false;
    private Texture specmap;
    private boolean hasSpecMap = false;
    
    public MatDrawnObject(FloatBuffer b, int vertsize){
        d = new DrawnObject(b,vertsize);
    }
    
    public MatDrawnObject(List<FloatBuffer> buffers, int vertSize){
        d = new DrawnObject(buffers,vertSize);
    }
    
    public MatDrawnObject(FloatBuffer b, IntBuffer index){
        d = new DrawnObject(b,index);
    }
    

    public void setTexture(Texture d){
        this.tex = d;
    }
    public void setNormalMap(Texture d){
        this.normalmap = d;
        this.hasNormalMap = true;
        
    }
    public void setSpecularMap(Texture d){
        this.specmap = d;
        this.hasSpecMap = true;   
    }
    public void setShaderMatrix(Matrix4f m){
        d.setShaderMatrix(m);
    }
    @Override
    public void draw() {
        if(tex != null)tex.useTexture(0);
        if(hasSpecMap) specmap.useTexture(4);   
        if(hasNormalMap) normalmap.useTexture(3);
        GlobalInfo.main.passMaterial(m,hasSpecMap, hasNormalMap);
        d.draw();
    }

    @Override
    public void saveShadowMVP() {
       d.saveShadowMVP();
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
