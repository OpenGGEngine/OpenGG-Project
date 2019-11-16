/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.drawn;

import com.opengg.core.model.Material;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayFormat;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

/**
 *
 * @author Javier
 */
public class MaterialRenderable implements Renderable {
    private Renderable d;
    private Material mat = Material.defaultmaterial;

    public MaterialRenderable(Renderable d){
        this.d = d;
    }
    
    public MaterialRenderable(Renderable d, Material m){
        this.d = d;
        this.mat = m;
        m.loadTextures();
    }
    
    public MaterialRenderable(FloatBuffer b, VertexArrayFormat format){
        d = new DrawnObject(format, b);
    }
    
    public MaterialRenderable(List<FloatBuffer> buffers, VertexArrayFormat format){
        d = new DrawnObject(format, buffers.toArray(new FloatBuffer[0]));
    }
    
    public MaterialRenderable(FloatBuffer b, IntBuffer index, Material m){
        d = new DrawnObject(index, b);
        this.mat = m;
        m.loadTextures();
    }
    
    public MaterialRenderable(FloatBuffer b, IntBuffer index){
        this(b, index, Material.defaultmaterial);
    }

    public void setMaterial(Material m) {
        this.mat = m;
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
        if(mat.em != null)
            mat.em.use(9);

        ShaderController.passMaterial(mat);

        d.render();
    }

}
