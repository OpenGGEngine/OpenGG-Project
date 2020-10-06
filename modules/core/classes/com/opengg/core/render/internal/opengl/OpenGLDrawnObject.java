package com.opengg.core.render.internal.opengl;

import com.opengg.core.engine.PerformanceManager;
import com.opengg.core.exceptions.RenderException;
import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayFormat;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL42.glDrawElementsInstancedBaseVertexBaseInstance;

public class OpenGLDrawnObject extends DrawnObject {

    public OpenGLDrawnObject(VertexArrayFormat format, IntBuffer index, FloatBuffer... vertices) {
        super(format, index, vertices);
    }

    public OpenGLDrawnObject(VertexArrayFormat format, GraphicsBuffer indexBuffer, int indexCount, GraphicsBuffer... vertices) {
        super(format, indexBuffer, indexCount, vertices);
    }

    @Override
    public void render(){
        if(RenderEngine.validateInitialization()) return;
        indexBuffer.bind();
        ShaderController.uploadModifiedDescriptorSets();
        if(!((OpenGLRenderer) RenderEngine.renderer).getCurrentVAO().getFormat().equals(format))
            throw new RenderException("Invalid VAO bound during render");
        ((OpenGLRenderer) RenderEngine.renderer).getCurrentVAO().applyFormat(vertexBufferObjects);
        PerformanceManager.registerDrawCall();
        glDrawElementsInstancedBaseVertexBaseInstance(getOpenGLDrawType(drawType), elementCount, getOpenGlIndexType(indexType), baseElement*2, instanceCount, baseVertex, 0);

    }

    private static int getOpenGlIndexType(IndexType type){
        return switch (type){
            case INT -> GL_UNSIGNED_INT;
            case SHORT -> GL_UNSIGNED_SHORT;
        };
    }

    private static int getOpenGLDrawType(DrawType type){
        return switch (type){
            case TRIANGLES -> GL_TRIANGLES;
            case TRIANGLE_STRIP -> GL_TRIANGLE_STRIP;
            case TRIANGLE_FAN -> GL_TRIANGLE_FAN;
            case POINTS -> GL_POINTS;
            case LINES -> GL_LINES;
            case LINE_STRIP -> GL_LINE_STRIP;
        };
    }
}
