/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.objects;

import com.opengg.core.model.Material;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.shader.CommonUniforms;
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
    private Material material = Material.defaultmaterial;

    public MaterialRenderable(Renderable d){
        this.d = d;
    }
    
    public MaterialRenderable(Renderable d, Material m){
        this.d = d;
        this.material = m;
        m.loadTextures();
    }
    
    public MaterialRenderable(FloatBuffer b, VertexArrayFormat format){
        d = DrawnObject.create(format, b);
    }
    
    public MaterialRenderable(List<FloatBuffer> buffers, VertexArrayFormat format){
        d = DrawnObject.create(format, buffers.toArray(new FloatBuffer[0]));
    }
    
    public MaterialRenderable(FloatBuffer b, IntBuffer index, Material m){
        d = DrawnObject.create(index, b);
        this.material = m;
        m.loadTextures();
    }
    
    public MaterialRenderable(FloatBuffer b, IntBuffer index){
        this(b, index, Material.defaultmaterial);
    }

    public void setMaterial(Material m) {
        this.material = m;
    }

    public Material getMaterial(){
        return material;
    }
    
    @Override
    public void render() {
        if(material.Kd != null)
            ShaderController.setUniform("Kd", material.Kd);
        if(material.norm != null)
            ShaderController.setUniform("bump", material.norm);
        if(material.Ks != null)
            ShaderController.setUniform("Ks", material.Ks);
        if(material.Ns != null)
            ShaderController.setUniform("Ns", material.Ns);
        if(material.em != null)
            ShaderController.setUniform("em", material.em);

        CommonUniforms.passMaterial(material);

        d.render();
    }

}
