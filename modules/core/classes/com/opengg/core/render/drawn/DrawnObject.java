/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.math.Tuple;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.exceptions.RenderException;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.system.Allocator;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL42.glDrawElementsInstancedBaseVertexBaseInstance;

/**
 *
 * @author Javier
 */
public class DrawnObject implements Renderable {
    private List<GraphicsBuffer> vertexBufferObjects = new ArrayList<>();
    private GraphicsBuffer elementBuffer;
    private VertexArrayFormat format;
    private int elementcount;
    private int drawtype = GL_TRIANGLES;
    private int instanceCount = 1;
    private int basevertex = 0;

    /**
     * Creates a drawn object containing the given {@link FloatBuffer FloatBuffers}
     * <br>
     * The given buffers are bound to the default {@link com.opengg.core.render.shader.VertexArrayObject} in order of appearance
     * @param vertices Buffers to add
     */
    public DrawnObject(FloatBuffer... vertices){
        this(RenderEngine.getDefaultFormat(), vertices);
    }

    /**
     * Creates a drawn object containing the given {@link FloatBuffer FloatBuffers}
     * <br>
     * The given buffers are bound to the given {@link com.opengg.core.render.shader.VertexArrayObject} in order of appearance
     * @param vertices Buffers to add
     * @param format VertexArrayFormat to use for rendering this object
     */
    public DrawnObject(VertexArrayFormat format, FloatBuffer... vertices){
        this(format, null, vertices);
    }

    /**
     * Creates a drawn object containing the given {@link FloatBuffer FloatBuffers}, indexed by the given {@link IntBuffer}
     * <br>
     * The given buffers are bound to the default {@link com.opengg.core.render.shader.VertexArrayObject} in order of appearance
     * @param vertices Buffers to add
     * @param index Buffer containing the indices to use for the vertices of the object
     */
    public DrawnObject(IntBuffer index, FloatBuffer... vertices){
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
    public DrawnObject(VertexArrayFormat format, IntBuffer index, FloatBuffer... vertices){
        this.format = format;
        defBuffers(vertices, index);
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

    private void defBuffers(FloatBuffer[] buffers, IntBuffer ind ){
        vertexBufferObjects.clear();

        for(var buffer : buffers){
            GraphicsBuffer vbo = GraphicsBuffer.allocate(GraphicsBuffer.BufferType.VERTEX_ARRAY_BUFFER, buffer, GraphicsBuffer.UsageType.STATIC_DRAW);
            vertexBufferObjects.add(vbo);
        }

        var indexbuffer = validateIndexBuffer(format, ind, buffers);

        elementBuffer = GraphicsBuffer.allocate(GraphicsBuffer.BufferType.ELEMENT_ARRAY_BUFFER, GraphicsBuffer.UsageType.STATIC_DRAW);
        elementBuffer.bind();
        elementBuffer.uploadData(indexbuffer.x);
        if(indexbuffer.y) Allocator.popStack();
    }

    private Tuple<IntBuffer, Boolean> validateIndexBuffer(VertexArrayFormat format, IntBuffer index, FloatBuffer[] vertices){
        var finalindex = index;

        if(index == null){
            int size = format.getVertexLength();
            elementcount = vertices[0].limit()/size;
            if(elementcount < 1024)
                finalindex = Allocator.stackAllocInt(elementcount);
            else
                finalindex = Allocator.allocInt(elementcount);

            for(int i = 0; i < elementcount; i++){
                finalindex.put(i);
            }
            finalindex.flip();

            if(elementcount < 1024)
                return Tuple.of(finalindex, true);
            else
                return Tuple.of(finalindex, false);
        }else{
            elementcount = index.limit();
            return Tuple.of(index, false);
        }
    }

    public void setRenderType(int type){
        this.drawtype = type;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
    }

    @Override
    public void render(){
        if(RenderEngine.validateInitialization()) return;

        elementBuffer.bind();

        if(!RenderEngine.getCurrentVAO().getFormat().equals(format))
            throw new RenderException("Invalid VAO bound during render");

        RenderEngine.getCurrentVAO().applyFormat(vertexBufferObjects);
        glDrawElementsInstancedBaseVertexBaseInstance(drawtype, elementcount, GL_UNSIGNED_INT, 0, instanceCount, basevertex, 0);

    }
}
