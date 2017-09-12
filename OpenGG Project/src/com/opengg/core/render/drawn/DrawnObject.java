/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.engine.RenderEngine;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.GLBuffer;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayFormat;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL32.GL_TRIANGLES_ADJACENCY;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Javier
 */
public class DrawnObject implements Drawable {
    GLBuffer vbo;
    GLBuffer evbo;
    FloatBuffer vertices;
    IntBuffer ind; 
    boolean adj = false;
    boolean vbexist = false, evbexist = false;
    int limit;
    int vertLimit;
    int drawtype = GL_TRIANGLES;
    
    Matrix4f model = Matrix4f.translate(0, 0, 0);
   
    public DrawnObject(FloatBuffer vertices, VertexArrayFormat format){
       
        limit = vertices.limit();
        vertLimit = limit/format.getVertexLength();
        
        ind = MemoryUtil.memAllocInt(vertLimit);
        for(int i = 0; i < vertLimit; i++){
            ind.put(i);
        }
        
        ind.flip();
        defBuffers(vertices, ind);
    }
    
    public DrawnObject(FloatBuffer b){
        this(b, RenderEngine.getDefaultFormat());
    }
    
    DrawnObject(List<FloatBuffer> buffers, VertexArrayFormat format){
      
        for(FloatBuffer b: buffers){
        
            limit = b.limit();
            vertLimit = limit/format.getVertexLength();

            ind = MemoryUtil.memAllocInt(vertLimit);
            for(long i = 0; i < vertLimit; i++){
                ind.put((int) i);
            }
            ind.flip();

            this.vertices = b;
        }
        
        defBuffers(vertices, ind);
    }
    
    public DrawnObject(List<FloatBuffer> buffers){
        this(buffers, RenderEngine.getDefaultFormat());
    }
    
    public DrawnObject(FloatBuffer vertices, IntBuffer index, VertexArrayFormat format){
        limit = vertices.limit();
        ind = index;
        
        defBuffers(vertices, ind);
    }
    
    public DrawnObject(FloatBuffer vertices, IntBuffer index){
        this(vertices, index, RenderEngine.getDefaultFormat());
    }
          
    private void defBuffers(FloatBuffer b, IntBuffer ind ){
        vbo = new GLBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW);
        vbo.bind();
        vbo.uploadData(b);

        evbo = new GLBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW);
        evbo.bind();
        evbo.uploadData(ind);
    }
    
    public void setAdjacency(boolean adj){
        this.adj = adj;
    }
    
    @Override
    public void setMatrix(Matrix4f model){
        this.model = model;
    }

    public void setRenderType(int type){
        this.drawtype = type;
    }

    public void setBuffer(FloatBuffer vertices, int vertSize){
        limit = vertices.limit();
        vertLimit = limit/vertSize;
        
        ind = MemoryUtil.memAllocInt(vertLimit);
        for(long i = 0; i < vertLimit; i++){
            ind.put((int) i);
        }
        ind.flip();
        
        this.vertices = vertices;
        defBuffers(vertices, ind);
    }    
    
    public FloatBuffer getVertexBuffer(){
        return vertices;
    }
    
    public IntBuffer getElementBuffer(){
        return ind;
    }
    
    public GLBuffer getGLVertexBuffer(){
        return vbo;
    }
    
    public GLBuffer getGLElementBuffer(){
        return evbo;
    }
    
    @Override
    public void render(){  
        ShaderController.setModel(model);
        RenderEngine.getCurrentVAO().applyFormat(vbo);
        evbo.bind();
        glDrawElements(drawtype, ind.limit(), GL_UNSIGNED_INT, 0);
    }

    @Override
    public void destroy() {
        vbo.delete();
        evbo.delete();
    }
}
