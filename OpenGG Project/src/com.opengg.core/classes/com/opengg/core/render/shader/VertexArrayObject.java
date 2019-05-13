/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.RenderEngine;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL43.*;

/**
 *
 * @author Javier
 */
public class VertexArrayObject {
    VertexArrayFormat format;
    PureVertexArrayObject vao;

    public VertexArrayObject(VertexArrayFormat format){
        vao = PureVertexArrayObject.create();
        vao.bind();
        this.format = format;
        generateFormatBindingPoints();
        vao.unbind();
    }

    public VertexArrayFormat getFormat(){
        return format;
    }

    private void generateFormatBindingPoints(){
        for(var binding : format.getBindings()){
            for(var attrib : binding.getAttributes()){
                int bytes = 1;
                if(attrib.type == GL_FLOAT) bytes = Float.BYTES;
                if(attrib.type == GL_INT) bytes = Integer.BYTES;
                if(attrib.type == GL_BYTE) bytes = 1;
                glEnableVertexAttribArray(ShaderController.getVertexAttributeIndex(attrib.name));
                glVertexAttribFormat(ShaderController.getVertexAttributeIndex(attrib.name), attrib.size, attrib.type, false, attrib.offset * bytes);
                glVertexAttribBinding(ShaderController.getVertexAttributeIndex(attrib.name), binding.getBindingIndex());
            }
            glVertexBindingDivisor(binding.getBindingIndex(), binding.getDivisor());
        }
    }

    public void applyFormat(List<GraphicsBuffer> buffers){
       for(var binding : getFormat().getBindings()){
           buffers.get(binding.getBindingIndex()).bindToAttribute(binding.getBindingIndex(), binding.getVertexSize());
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
