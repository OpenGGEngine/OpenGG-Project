/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.engine.RenderEngine;
import com.opengg.core.render.GLBuffer;
import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.glGetError;

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
            int bytes = 1;
            if(attrib.type == GL_FLOAT) bytes = Float.BYTES;
            if(attrib.type == GL_INT) bytes = Integer.BYTES;
            if(attrib.type == GL_BYTE) bytes = 1;

            ShaderController.enableVertexAttribute(attrib.name);
            ShaderController.pointVertexAttribute(attrib.name, attrib.size, attrib.type, attrib.buflength * Float.BYTES, attrib.offset * bytes);
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
