/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.internal.opengl.OpenGLDrawnObject;
import com.opengg.core.render.internal.vulkan.VulkanDrawnObject;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.system.Allocator;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public abstract sealed class DrawnObject implements Renderable permits OpenGLDrawnObject, VulkanDrawnObject {
    protected List<GraphicsBuffer> vertexBufferObjects = new ArrayList<>();
    protected GraphicsBuffer indexBuffer;
    protected VertexArrayFormat format;

    protected IndexType indexType = IndexType.INT;
    protected DrawType drawType = DrawType.TRIANGLES;
    protected int elementCount;
    protected int instanceCount = 1;
    protected int baseVertex = 0;
    protected int baseElement = 0;


    public static DrawnObject create(FloatBuffer... vertices) {
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLDrawnObject(RenderEngine.getDefaultFormat(), null, vertices);
            case VULKAN -> new VulkanDrawnObject(RenderEngine.getDefaultFormat(), null,vertices);
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
            case OPENGL -> new OpenGLDrawnObject(RenderEngine.getDefaultFormat(), index, vertices);
            case VULKAN -> new VulkanDrawnObject(RenderEngine.getDefaultFormat(), index, vertices);
        };
    }

    public static DrawnObject create(VertexArrayFormat format, FloatBuffer... vertices) {
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLDrawnObject(format, null, vertices);
            case VULKAN -> new VulkanDrawnObject(format, null, vertices);
        };
    }

    public static DrawnObject createFromGPUMemory(VertexArrayFormat format, GraphicsBuffer index, int indexCount, GraphicsBuffer... vertices) {
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLDrawnObject(format, index, indexCount, vertices);
            case VULKAN -> new VulkanDrawnObject(format, index, indexCount, vertices);
        };
    }

    protected DrawnObject(){}

    protected DrawnObject(VertexArrayFormat format, IntBuffer indexBuffer, Buffer... vertices){
        this.format = format;
        generateGPUMemory(vertices, indexBuffer);
    }

    protected DrawnObject(VertexArrayFormat format, GraphicsBuffer indexBuffer, int indexCount, GraphicsBuffer... vertices){
        this.format = format;
        this.indexBuffer = indexBuffer;
        this.elementCount = indexCount;
        this.vertexBufferObjects = new ArrayList<>(List.of(vertices));
    }

    /**
     * Sets the buffer with the given ID to the given {@link FloatBuffer}
     * <br>
     * This sets the buffer to the given buffer type, optimizing for reading operations
     * @param bufferIndex Buffer to replace
     * @param buffer Buffer to update
     */
    public void updateBuffer(int bufferIndex, Buffer buffer){
        vertexBufferObjects.set(bufferIndex, generateVertexBuffer(buffer));
    }

    protected void generateGPUMemory(Buffer[] buffers, IntBuffer indexBuffer){
        vertexBufferObjects.clear();

        for(var buffer : buffers){
            vertexBufferObjects.add(generateVertexBuffer(buffer));
        }

        int bufferSize = 0;
        var mainBuffer = buffers[0];
        if(mainBuffer instanceof ByteBuffer bbuf){
            bufferSize = bbuf.limit();
        }else if(mainBuffer instanceof FloatBuffer fbuf){
            bufferSize = fbuf.limit()*Float.BYTES;
        }else if(mainBuffer instanceof IntBuffer ibuf){
            bufferSize = ibuf.limit()*Integer.BYTES;
        }

        if(indexBuffer == null){
            this.indexBuffer = generateIndexBuffer(format, bufferSize);
        }else{
            this.indexBuffer = GraphicsBuffer.allocate(GraphicsBuffer.BufferType.ELEMENT_ARRAY_BUFFER, indexBuffer, GraphicsBuffer.UsageType.NONE);
            this.elementCount = indexBuffer.limit();
        }
    }

    private GraphicsBuffer generateVertexBuffer(Buffer buffer){
        GraphicsBuffer vbo;
        if(buffer instanceof ByteBuffer bbuf){
            vbo = GraphicsBuffer.allocate(GraphicsBuffer.BufferType.VERTEX_ARRAY_BUFFER, bbuf, GraphicsBuffer.UsageType.NONE);
        }else if(buffer instanceof FloatBuffer fbuf){
            vbo = GraphicsBuffer.allocate(GraphicsBuffer.BufferType.VERTEX_ARRAY_BUFFER, fbuf, GraphicsBuffer.UsageType.NONE);
        }else if(buffer instanceof IntBuffer ibuf){
            vbo = GraphicsBuffer.allocate(GraphicsBuffer.BufferType.VERTEX_ARRAY_BUFFER, ibuf, GraphicsBuffer.UsageType.NONE);
        }else{
            throw new IllegalArgumentException(buffer.getClass().getSimpleName() + " is not a useable buffer type");
        }
        return vbo;
    }

    private GraphicsBuffer generateIndexBuffer(VertexArrayFormat format, int primaryBufferSize){
            int vertexSize = format.getPrimaryVertexLength();
            elementCount = primaryBufferSize/vertexSize;
            IntBuffer finalIndex;
            if(elementCount < 1024)
                finalIndex = Allocator.stackAllocInt(elementCount);
            else
                finalIndex = Allocator.allocInt(elementCount);

            for(int i = 0; i < elementCount; i++){
                finalIndex.put(i);
            }
            finalIndex.flip();

            var gpuIndices = GraphicsBuffer.allocate(GraphicsBuffer.BufferType.ELEMENT_ARRAY_BUFFER, finalIndex, GraphicsBuffer.UsageType.NONE);

            if(elementCount < 1024)
                Allocator.popStack();

            return gpuIndices;
    }

    public DrawnObject setRenderType(DrawType type){
        this.drawType = type;
        return this;
    }

    public void setIndexType(IndexType type){
        this.indexType = type;
    }

    public void setBaseVertex(int baseVertex) {
        this.baseVertex = baseVertex;
    }

    public void setBaseElement(int baseElement) {
        this.baseElement = baseElement;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
    }


    public enum DrawType {
        TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, POINTS, LINES, LINE_STRIP
    }

    public enum IndexType{
        INT, SHORT
    }
}
