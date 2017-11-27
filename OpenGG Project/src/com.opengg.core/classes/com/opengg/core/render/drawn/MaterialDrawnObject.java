/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.drawn;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.model.Material;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayFormat;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

/**
 *
 * @author Javier
 */
public class MaterialDrawnObject implements Drawable {
    Drawable d;
    Material mat = Material.defaultmaterial;
    
    public void setM(Material m) {
        this.mat = m;
    }

    public MaterialDrawnObject(Drawable d){
        this.d = d;
    }
    
    public MaterialDrawnObject(Drawable d, Material m){
        this.d = d;
        this.mat = m;
        m.loadTextures();
    }
    
    public MaterialDrawnObject(FloatBuffer b, VertexArrayFormat format){
        d = new DrawnObject(b, format);
    }
    
    public MaterialDrawnObject(List<FloatBuffer> buffers, VertexArrayFormat format){
        d = new DrawnObject(buffers, format);
    }
    
    public MaterialDrawnObject(FloatBuffer b, IntBuffer index, Material m){
        d = new DrawnObject(b,index);
        this.mat = m;
        m.loadTextures();
    }
    
    public MaterialDrawnObject(FloatBuffer b, IntBuffer index){
        this(b, index, Material.defaultmaterial);
    }
    
    public Material getMaterial(){
        return mat;
    }
    
    @Override
    public void render() {
        if(mat.Kd != null)
            mat.Kd.use(0); 
        if(mat.norm != null) 
            mat.norm.use(3);
        if(mat.Ks != null) 
            mat.Ks.use(4); 
        if(mat.Ns != null)
            mat.Ns.use(5);

        ShaderController.passMaterial(mat);

        d.render();
    }

    @Override
    public void setMatrix(Matrix4f m) {
        d.setMatrix(m);
    }

    @Override
    public void destroy() {
        d.destroy();
    }
}
