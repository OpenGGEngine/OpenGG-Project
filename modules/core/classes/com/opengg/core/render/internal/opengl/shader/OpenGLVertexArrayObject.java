package com.opengg.core.render.internal.opengl.shader;

import com.opengg.core.GGInfo;
import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.internal.opengl.OpenGLBuffer;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayBinding;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.render.shader.VertexArrayObject;

import java.util.List;

import static org.lwjgl.opengl.GL43.*;

public class OpenGLVertexArrayObject implements VertexArrayObject {
    VertexArrayFormat format;
    NativeOpenGLVertexArrayObject vao;

    public OpenGLVertexArrayObject(VertexArrayFormat format){
        if(GGInfo.isServer()) return;
        this.format = format;

        vao = new NativeOpenGLVertexArrayObject();
        generateFormatBindingPoints();
    }

    public VertexArrayFormat getFormat(){
        return format;
    }

    private void generateFormatBindingPoints(){
        for(var binding : format.getBindings()){
            for(var attrib : binding.attributes()){
                int bytes = switch (attrib.type()){
                    case FLOAT, FLOAT4, FLOAT3, FLOAT2, INT -> 4;
                    case BYTE -> 1;
                };
                vao.enableAttribute(ShaderController.getVertexAttributeIndex(attrib.name()));
                vao.setAttributeFormat(ShaderController.getVertexAttributeIndex(attrib.name()), attrib.size()/bytes, glFormatFromType(attrib.type()), false, attrib.offset());
                vao.setAttributeBinding(ShaderController.getVertexAttributeIndex(attrib.name()), binding.bindingIndex());
            }
            vao.setBindingDivisor(binding.bindingIndex(), binding.divisor());
        }
    }

    public void applyVertexBuffers(List<GraphicsBuffer> buffers){
        for(var binding : getFormat().getBindings()){
            applyVertexBuffer(buffers.get(binding.bindingIndex()), binding.bindingIndex(), binding.vertexSize());
        }
    }

    @Override
    public void bind() {
        vao.bind();
    }

    private void applyVertexBuffer(GraphicsBuffer buffer, int binding, int vertexSize){
        vao.applyVertexBufferToBinding(((OpenGLBuffer)buffer).getID(), binding, vertexSize);
    }

    public void applyElementBuffer(GraphicsBuffer buffer){
        vao.applyElementBufferToBinding(((OpenGLBuffer)buffer).getID());
    }

    public void delete(){
        vao.delete();
    }

    private static int glFormatFromType(VertexArrayBinding.VertexArrayAttribute.Type type){
        return switch (type){
            case FLOAT, FLOAT4, FLOAT3, FLOAT2 -> GL_FLOAT;
            case INT -> GL_INT;
            case BYTE -> GL_BYTE;
        };
    }
}
