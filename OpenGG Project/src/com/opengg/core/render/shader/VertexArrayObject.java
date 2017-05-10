/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.engine.RenderEngine;
import com.opengg.core.render.GLBuffer;

/**
 *
 * @author Javier
 */
public class VertexArrayObject {
    VertexArrayFormat format;
    NativeVertexArrayObject vao;
    
    public VertexArrayObject(VertexArrayFormat format){
        vao = new NativeVertexArrayObject();
        this.format = format;
        findAttributes();
    }
    
    public VertexArrayFormat getFormat(){
        return format;
    }
    
    public void applyFormat(GLBuffer... buffers){
        vao.bind();
        int lastloc = 0;
        buffers[0].bind();
        for(VertexArrayAttribute attrib : format.attribs){
            if(attrib.arrayindex != lastloc){
                lastloc = attrib.arrayindex;
                buffers[lastloc].bind();
            }

            ShaderController.enableVertexAttribute(attrib.name);
            ShaderController.pointVertexAttribute(attrib.name, attrib.size, 12 * Float.BYTES, attrib.offset * Float.BYTES);
            ShaderController.setVertexAttribDivisor(attrib.name, attrib.divisor ? 1 : 0);
        }
    }

    public void findAttributes(){
        for(VertexArrayAttribute attrib : format.attribs){
            ShaderController.findAttribLocation(attrib.name);
        }
    }
    
    public void bind(){
        RenderEngine.setVAO(this);
        vao.bind();
    }
    
    public void unbind(){
        vao.unbind();
        RenderEngine.setVAO(null);
    }
    
    public void delete(){
        vao.delete();
    }
}
