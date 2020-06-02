/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.math.util.Tuple;
import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.internal.opengl.OpenGLDrawnObject;
import com.opengg.core.render.internal.vulkan.VulkanDrawnObject;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.system.Allocator;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public abstract class DrawnObject implements Renderable {
    protected List<GraphicsBuffer> vertexBufferObjects = new ArrayList<>();
    protected GraphicsBuffer indexBuffer;
    protected VertexArrayFormat format;

    protected DrawType drawType = DrawType.TRIANGLES;
    protected int elementCount;
    protected int instanceCount = 1;
    protected int baseVertex = 0;

    /**
     * Creates a drawn object containing the given {@link FloatBuffer FloatBuffers}
     * <br>
     * The given buffers are bound to the default {@link com.opengg.core.render.shader.VertexArrayObject} in order of appearance
     * @param vertices Buffers to add
     */
    protected DrawnObject(FloatBuffer... vertices){
        this(RenderEngine.getDefaultFormat(), vertices);
    }

    /**
     * Creates a drawn object containing the given {@link FloatBuffer FloatBuffers}
     * <br>
     * The given buffers are bound to the given {@link com.opengg.core.render.shader.VertexArrayObject} in order of appearance
     * @param vertices Buffers to add
     * @param format VertexArrayFormat to use for rendering this object
     */
    protected DrawnObject(VertexArrayFormat format, FloatBuffer... vertices){
        this(format, null, vertices);
    }

    /**
     * Creates a drawn object containing the given {@link FloatBuffer FloatBuffers}, indexed by the given {@link IntBuffer}
     * <br>
     * The given buffers are bound to the default {@link com.opengg.core.render.shader.VertexArrayObject} in order of appearance
     * @param vertices Buffers to add
     * @param index Buffer containing the indices to use for the vertices of the object
     */
    protected DrawnObject(IntBuffer index, FloatBuffer... vertices){
        this(RenderEngine.getDefaultFormat(), index, vertices);
    }

    /**
     * Creates a drawn object containing the given {@link FloatBuffer FloatBuffers}, indexed by the given {@link IntBuffer}
     * <br>
     * The given buffers are bound to the default {@link com.opengg.core.render.shader.VertexArrayObject} in order of appearance
     * @param vertices Buffers to add
     * @param index Buffer containing the indices to use for the vertices of the object
     * @param format VertexArrayFormat to add
     */
    protected DrawnObject(VertexArrayFormat format, IntBuffer index, FloatBuffer... vertices){
        this.format = format;
        defBuffers(vertices, index);
    }

    public static DrawnObject create(FloatBuffer... vertices) {
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLDrawnObject(vertices);
            case VULKAN -> new VulkanDrawnObject(vertices);
        };
    }

    public static DrawnObject create(VertexArrayFormat format, IntBuffer index, FloatBuffer... vertices) {
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLDrawnObject(format, index, vertices);
            case VULKAN -> new VulkanDrawnObject(format, index, vertices);
        };
    }

    public static DrawnObject create(IntBuffer index, FloatBuffer... vertices) {
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLDrawnObject(index, vertices);
            case VULKAN -> new VulkanDrawnObject(index, vertices);
        };
    }

    public static DrawnObject create(VertexArrayFormat format, FloatBuffer... vertices) {
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLDrawnObject(format, vertices);
            case VULKAN -> new VulkanDrawnObject(format, vertices);
        };
    }

    /**
     * Sets the buffer with the given ID to the given {@link FloatBuffer}
     * <br>
     * This sets the buffer to the default buffer type of GL_STATIC_DRAW, optimizing for reading operations
     * @param bufferid Buffer to replace
     * @param buffer Buffer to update
     */
    public void updateBuffer(int bufferid, FloatBuffer buffer){
        this.updateBuffer(bufferid, buffer, GraphicsBuffer.UsageType.STATIC_DRAW);
    }

    /**
     * Sets the buffer with the given ID to the given {@link FloatBuffer}
     * <br>
     * This sets the buffer to the given buffer type, optimizing for reading operations
     * @param bufferid Buffer to replace
     * @param buffer Buffer to update
     * @param buffertype Type of buffer to upload, of types GL_STATIC_DRAW, GL_DYNAMIC_DRAW, and GL_STREAM_DRAW
     */
    public void updateBuffer(int bufferid, FloatBuffer buffer, GraphicsBuffer.UsageType buffertype){
        GraphicsBuffer vbo = GraphicsBuffer.allocate(GraphicsBuffer.BufferType.VERTEX_ARRAY_BUFFER, buffer, buffertype);
        vertexBufferObjects.set(bufferid, vbo);
    }

    private void defBuffers(FloatBuffer[] buffers, IntBuffer ind){
        vertexBufferObjects.clear();

        for(var buffer : buffers){
            GraphicsBuffer vbo = GraphicsBuffer.allocate(GraphicsBuffer.BufferType.VERTEX_ARRAY_BUFFER, buffer, GraphicsBuffer.UsageType.STATIC_DRAW);
            vertexBufferObjects.add(vbo);
        }

        var indices = validateIndexBuffer(format, ind, buffers);
        indexBuffer = GraphicsBuffer.allocate(GraphicsBuffer.BufferType.ELEMENT_ARRAY_BUFFER, indices.x(), GraphicsBuffer.UsageType.STATIC_DRAW);
        if(indices.y()) Allocator.popStack();
    }

    private Tuple<IntBuffer, Boolean> validateIndexBuffer(VertexArrayFormat format, IntBuffer index, FloatBuffer[] vertices){
        if(index == null){
            int size = format.getVertexLength();
            elementCount = vertices[0].limit()/(size/Float.BYTES);
            IntBuffer finalIndex;
            if(elementCount < 1024)
                finalIndex = Allocator.stackAllocInt(elementCount);
            else
                finalIndex = Allocator.allocInt(elementCount);

            for(int i = 0; i < elementCount; i++){
                finalIndex.put(i);
            }
            finalIndex.flip();

            if(elementCount < 1024)
                return Tuple.of(finalIndex, true);
            else
                return Tuple.of(finalIndex, false);
        }else{
            elementCount = index.limit();
            return Tuple.of(index, false);
        }
    }

    public void setRenderType(DrawType type){
        this.drawType = type;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
    }


    public enum DrawType {
        TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, POINTS, LINES, LINE_STRIP
    }
}
