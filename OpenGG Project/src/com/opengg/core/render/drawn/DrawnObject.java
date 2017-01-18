/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.engine.RenderEngine;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.render.shader.ShaderController;
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
    VertexBufferObject vbo;
    VertexBufferObject evbo;
    FloatBuffer b;
    IntBuffer ind; 
    boolean adj = false;
    int limit;
    int vertLimit;
    
    Matrix4f model = Matrix4f.translate(0, 0, 0);
    
    static{
        DrawnObjectHandler.setup();
    }
   
    DrawnObject(FloatBuffer b, int vertSize){
       
        limit = b.limit();
        vertLimit = limit/vertSize;
        
        ind = MemoryUtil.memAllocInt(vertLimit);
        for(long i = 0; i < vertLimit; i++){
            ind.put((int) i);
        }
        ind.flip();
        
        this.b = b;
        defBuffers(b, ind);

        removeBuffer();
    }
    
    public DrawnObject(FloatBuffer b){
        this(b, 12);
    }
    
    DrawnObject(List<FloatBuffer> buffers, int vertSize){
      
        for(FloatBuffer b: buffers){
        
            limit = b.limit();
            vertLimit = limit/vertSize;

            ind = MemoryUtil.memAllocInt(vertLimit);
            for(long i = 0; i < vertLimit; i++){
                ind.put((int) i);
            }
            ind.flip();

            this.b = b;
        }
        
        defBuffers(b, ind);
        removeBuffer();
    }
    
    public DrawnObject(List<FloatBuffer> buffers){
        this(buffers, 12);
    }
    
    public DrawnObject(FloatBuffer b, IntBuffer index){
        
        limit = b.limit();
        vertLimit = limit/12;
        ind = index;
        
        this.b = b;
        defBuffers(b, ind);

    }
    
    private void defBuffers(FloatBuffer b, IntBuffer ind ){
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);
        vbo.uploadData(GL_ARRAY_BUFFER, b, GL_STATIC_DRAW);
        
        evbo = new VertexBufferObject();
        evbo.bind(GL_ELEMENT_ARRAY_BUFFER);
        evbo.uploadData(GL_ELEMENT_ARRAY_BUFFER, ind, GL_STATIC_DRAW);
    }
    
    private void pointAttrib(){
        ShaderController.pointVertexAttributes();
    }
    
    private void defAttrib(){
        ShaderController.defVertexAttributes();
    }
    
    public void setAdjacency(boolean adj){
        this.adj = adj;
    }
    
    @Override
    public void setMatrix(Matrix4f model){
        this.model = model;
    }


    public void setBuffer(FloatBuffer b, int vertSize){
        limit = b.limit();
        vertLimit = limit/vertSize;
        
        ind = MemoryUtil.memAllocInt(vertLimit);
        for(long i = 0; i < vertLimit; i++){
            ind.put((int) i);
        }
        ind.flip();
        
        this.b = b;
        defBuffers(b, ind);
        
        removeBuffer();
    }    
    
    public FloatBuffer getBuffer(){
        return b;
    }
    
    public IntBuffer getElementBuffer(){
        return ind;
    }
    
    @Override
    public void draw(){    
        ShaderController.setModel(model);  
        vbo.bind(GL_ARRAY_BUFFER);
        evbo.bind(GL_ELEMENT_ARRAY_BUFFER);
        defAttrib();
        pointAttrib();
        glDrawElements(adj ? GL_TRIANGLES_ADJACENCY : GL_TRIANGLES, ind.limit(), GL_UNSIGNED_INT, 0);       
    }
    
    public void removeBuffer(){
        //b = null;
    }

    @Override
    public Matrix4f getMatrix() {
        return model;
    }

    @Override
    public void destroy() {
        removeBuffer();
    }
}
