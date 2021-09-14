/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.objects;

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
    protected boolean enforce = true;


    public static DrawnObject create(Buffer... vertices) {
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLDrawnObject(RenderEngine.getDefaultFormat(), null, vertices);
            case VULKAN -> new VulkanDrawnObject(RenderEngine.getDefaultFormat(), null, vertices);
        };
    }

    public static DrawnObject create(VertexArrayFormat format, IntBuffer index, Buffer... vertices) {
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLDrawnObject(format, index, vertices);
            case VULKAN -> new VulkanDrawnObject(format, index, vertices);
        };
    }

    public static DrawnObject create(IntBuffer index, Buffer... vertices) {
        return switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLDrawnObject(RenderEngine.getDefaultFormat(), index, vertices);
            case VULKAN -> new VulkanDrawnObject(RenderEngine.getDefaultFormat(), index, vertices);
        };
    }

    public static DrawnObject create(VertexArrayFormat format, Buffer... vertices) {
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
     * Sets the buffer with the given ID to the given {@link Buffer}
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

        var mainBuffer = buffers[0];
        int bufferSize = switch (mainBuffer) {
            case ByteBuffer bbuf -> bbuf.limit();
            case FloatBuffer fbuf -> fbuf.limit() * Float.BYTES;
            case IntBuffer ibuf -> ibuf.limit() * Integer.BYTES;
            case null, default -> {
                yield 0;
            }
        };

        if(indexBuffer == null){
            this.indexBuffer = generateIndexBuffer(format, bufferSize);
        }else{
            this.indexBuffer = GraphicsBuffer.allocate(GraphicsBuffer.BufferType.ELEMENT_ARRAY_BUFFER, indexBuffer, GraphicsBuffer.UsageType.NONE);
            this.elementCount = indexBuffer.limit();
        }
    }

    private GraphicsBuffer generateVertexBuffer(Buffer buffer){
        return switch (buffer) {
            case ByteBuffer bbuf -> GraphicsBuffer.allocate(GraphicsBuffer.BufferType.VERTEX_ARRAY_BUFFER, bbuf, GraphicsBuffer.UsageType.NONE);
            case FloatBuffer fbuf -> GraphicsBuffer.allocate(GraphicsBuffer.BufferType.VERTEX_ARRAY_BUFFER, fbuf, GraphicsBuffer.UsageType.NONE);
            case IntBuffer ibuf -> GraphicsBuffer.allocate(GraphicsBuffer.BufferType.VERTEX_ARRAY_BUFFER, ibuf, GraphicsBuffer.UsageType.NONE);
            case null, default -> throw new IllegalArgumentException(buffer.getClass().getSimpleName() + " is not a useable buffer type");
        };
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

    public void setFormat(VertexArrayFormat format) {
        this.format = format;
    }

    public void shouldEnforceFormat(boolean enforce){
        this.enforce = enforce;
    }

    public enum DrawType {
        TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, POINTS, LINES, LINE_STRIP
    }

    public enum IndexType{
        INT, SHORT
    }
}
