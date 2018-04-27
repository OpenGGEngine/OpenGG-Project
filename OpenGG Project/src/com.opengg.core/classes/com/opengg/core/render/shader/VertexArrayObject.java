/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.engine.RenderEngine;
import com.opengg.core.render.GraphicsBuffer;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;

/**
 *
 * @author Javier
 */
public class VertexArrayObject {
    VertexArrayFormat format;
    PureVertexArrayObject vao;
    
    public VertexArrayObject(VertexArrayFormat format){
        vao = PureVertexArrayObject.create();
        this.format = format;
        findAttributes();
    }
    
    public VertexArrayFormat getFormat(){
        return format;
    }

    public void applyFormat(GraphicsBuffer... buffers){
        this.applyFormat(List.of(buffers));
    }

    public void applyFormat(List<GraphicsBuffer> buffers){
        vao.bind();
        int lastloc = 0;
        buffers.get(0).bind();
        for(VertexArrayAttribute attrib : format.attribs){
            if(attrib.arrayindex != lastloc){
                lastloc = attrib.arrayindex;
                buffers.get(lastloc).bind();
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
        for(VertexArrayAttribute attribute : format.attribs){
            ShaderController.findAttribLocation(attribute.name);
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
