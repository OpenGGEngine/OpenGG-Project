/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.exceptions.RenderException;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.system.Allocator;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glGetError;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL42.glDrawElementsInstancedBaseVertexBaseInstance;

/**
 *
 * @author Javier
 */
public class DrawnObject implements Drawable {
    private List<GraphicsBuffer> vertexBufferObjects = new ArrayList<>();
    private GraphicsBuffer elementBuffer;
    private VertexArrayFormat format;
    private int elementcount;
    private int drawtype = GL_TRIANGLES;
    private int instancecount = 1;
    private int basevertex = 0;
    
    Matrix4f model = Matrix4f.translate(0, 0, 0);

    public DrawnObject(FloatBuffer... b){
        this(RenderEngine.getDefaultFormat(), b);
    }

    public DrawnObject(VertexArrayFormat format, FloatBuffer... vertices){
        this(format, null, vertices);
    }

    public DrawnObject(IntBuffer index, FloatBuffer... vertices){
        this(RenderEngine.getDefaultFormat(), index, vertices);
    }

    public DrawnObject(VertexArrayFormat format, IntBuffer index, FloatBuffer... vertices){
        this.format = format;
        defBuffers(vertices, index);
    }

    public void updateBuffer(int bufferid, FloatBuffer buffer){
        this.updateBuffer(bufferid, buffer, GL_STATIC_DRAW);
    }

    public void updateBuffer(int bufferid, FloatBuffer buffer, int buffertype){
        GraphicsBuffer vbo = createGLBuffer(buffer, buffertype);
        vertexBufferObjects.set(bufferid, vbo);
    }

    private GraphicsBuffer createGLBuffer(FloatBuffer buffer, int buffertype){
        var vbo = GraphicsBuffer.allocate(GL_ARRAY_BUFFER, buffertype);
        vbo.bind();
        vbo.uploadData(buffer);
        return vbo;
    }

    private void defBuffers(FloatBuffer[] buffers, IntBuffer ind ){
        for(GraphicsBuffer graphicsBuffer : vertexBufferObjects){
            graphicsBuffer.delete();
        }

        vertexBufferObjects.clear();

        for(var buffer : buffers){
            GraphicsBuffer vbo = createGLBuffer(buffer, GL_STATIC_DRAW);
            vertexBufferObjects.add(vbo);
        }

        var indexbuffer = validateIndexBuffer(format, ind, buffers);

        elementBuffer = GraphicsBuffer.allocate(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW);
        elementBuffer.bind();
        elementBuffer.uploadData(indexbuffer);
    }

    private IntBuffer validateIndexBuffer(VertexArrayFormat format, IntBuffer index, FloatBuffer[] vertices){
        var finalindex = index;

        if(index == null){
            int size = format.getVertexLength();
            elementcount = vertices[0].limit()/size;
            finalindex = Allocator.allocInt(elementcount);
            for(int i = 0; i < elementcount; i++){
                finalindex.put(i);
            }
            finalindex.flip();
        }else{
            elementcount = index.limit();
        }
        return finalindex;
    }

    @Override
    public void setMatrix(Matrix4f model){
        this.model = model;
    }

    public void setRenderType(int type){
        this.drawtype = type;
    }

    @Override
    public void render(){

        if(!RenderEngine.validateInitialization()) return;

        ShaderController.setModel(model);

        elementBuffer.bind();

        if(!RenderEngine.getCurrentVAO().getFormat().equals(format))
            throw new RenderException("Invalid VAO bound during render");
        RenderEngine.getCurrentVAO().applyFormat(vertexBufferObjects);

        glDrawElementsInstancedBaseVertexBaseInstance(drawtype, elementcount, GL_UNSIGNED_INT, 0, instancecount, basevertex, 0);
    }

    @Override
    public void destroy() {
        for(GraphicsBuffer vbo : vertexBufferObjects){
            vbo.delete();
        }
        elementBuffer.delete();
    }
}
