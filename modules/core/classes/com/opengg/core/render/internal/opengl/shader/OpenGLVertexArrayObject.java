package com.opengg.core.render.internal.opengl.shader;

import com.opengg.core.GGInfo;
import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.internal.opengl.OpenGLRenderer;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.render.shader.VertexArrayObject;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL43.*;

public class OpenGLVertexArrayObject implements VertexArrayObject {
    VertexArrayFormat format;
    NativeOpenGLVertexArrayObject vao;

    public OpenGLVertexArrayObject(VertexArrayFormat format){
        if(GGInfo.isServer()) return;
        vao = new NativeOpenGLVertexArrayObject();
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
                int bytes = switch (attrib.type()){
                    case FLOAT, FLOAT4, FLOAT3, FLOAT2, INT -> 4;
                    case BYTE -> 1;
                };
                glEnableVertexAttribArray(ShaderController.getVertexAttributeIndex(attrib.name()));
                glVertexAttribFormat(ShaderController.getVertexAttributeIndex(attrib.name()), attrib.size()/bytes, glFormatFromType(attrib.type()), false, attrib.offset());
                glVertexAttribBinding(ShaderController.getVertexAttributeIndex(attrib.name()), binding.getBindingIndex());
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
        ((OpenGLRenderer) RenderEngine.renderer).setVAO(this);
        vao.bind();
    }

    public void unbind(){
        vao.unbind();
        ((OpenGLRenderer)RenderEngine.renderer).setVAO(null);
    }

    public void delete(){
        vao.delete();
    }

    private static int glFormatFromType(VertexArrayFormat.VertexArrayAttribute.Type type){
        return switch (type){
            case FLOAT, FLOAT4, FLOAT3, FLOAT2 -> GL_FLOAT;
            case INT -> GL_INT;
            case BYTE -> GL_BYTE;
        };
    }
}
