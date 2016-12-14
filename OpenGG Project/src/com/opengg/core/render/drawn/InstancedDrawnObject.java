/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.render.VertexBufferObject;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;

/**
 *
 * @author Javier
 */
public class InstancedDrawnObject implements Drawable {
    VertexBufferObject vbo, evbo, ivbo;
    long offset;
    FloatBuffer b;
    IntBuffer ind; 
    boolean hasmat;
    int limit;
    int vertLimit;
    long vertOffset;
    int instnum;
    Matrix4f model = Matrix4f.translate(0, 0, 0);
    Matrix4f shadeModel = new Matrix4f();
    
    static{
        DrawnObjectHandler.setup();
    }
    
    public InstancedDrawnObject(FloatBuffer b,  int vertSize, FloatBuffer inst){
       
        limit = b.limit();
        offset = DrawnObjectHandler.getOffset();
        vertLimit = limit/vertSize;
        vertOffset = offset/vertSize;
        
        ind = MemoryUtil.memAllocInt(vertLimit);
        for(long i = vertOffset; i < vertLimit; i++){
            ind.put((int) i);
        }
        ind.flip();
        
        instnum = inst.limit()/4;
        
        this.b = b;
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);
        vbo.uploadData(GL_ARRAY_BUFFER, b, GL_STATIC_DRAW);
        
        evbo = new VertexBufferObject();
        evbo.bind(GL_ELEMENT_ARRAY_BUFFER);
        evbo.uploadData(GL_ELEMENT_ARRAY_BUFFER, ind, GL_STATIC_DRAW);
        
        ivbo = new VertexBufferObject();
        ivbo.bind(GL_ARRAY_BUFFER);
        ivbo.uploadData(GL_ARRAY_BUFFER, inst, GL_STATIC_DRAW);
        
        RenderEngine.controller.defVertexAttributes();
        RenderEngine.controller.pointVertexAttributes();
        this.removeBuffer();
    }
    public InstancedDrawnObject(FloatBuffer b, FloatBuffer inst){
        this(b, 12, inst);
    }
    
    public InstancedDrawnObject(List<FloatBuffer> buffers, FloatBuffer inst, int vertSize){
      
        for(FloatBuffer b: buffers){
        
            limit = b.limit();
            offset = DrawnObjectHandler.getOffset();
            vertLimit = limit/vertSize;
            vertOffset = offset/vertSize;

            ind = MemoryUtil.memAllocInt(vertLimit);
            for(long i = vertOffset; i < vertLimit; i++){
                ind.put((int) i);
            }
            ind.flip();

            this.b = b;
            vbo = new VertexBufferObject();
            vbo.bind(GL_ARRAY_BUFFER);
            vbo.uploadData(GL_ARRAY_BUFFER, b, GL_STATIC_DRAW);
        }
        
        RenderEngine.controller.defVertexAttributes();
        RenderEngine.controller.pointVertexAttributes();
        this.removeBuffer();
    }
    
    public InstancedDrawnObject(FloatBuffer b, IntBuffer index, FloatBuffer inst){
        
        limit = b.limit();
        offset = DrawnObjectHandler.getOffset();
        vertLimit = limit/12;
        vertOffset = offset/12;
        ind = index;
        
        instnum = inst.limit()/4;
        
        this.b = b;
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);
        vbo.uploadData(GL_ARRAY_BUFFER, b, GL_STATIC_DRAW);
        
        evbo = new VertexBufferObject();
        evbo.bind(GL_ELEMENT_ARRAY_BUFFER);
        evbo.uploadData(GL_ELEMENT_ARRAY_BUFFER, ind, GL_STATIC_DRAW);
        
        ivbo = new VertexBufferObject();
        ivbo.bind(GL_ARRAY_BUFFER);
        ivbo.uploadData(GL_ARRAY_BUFFER, inst, GL_STATIC_DRAW);
        
        RenderEngine.controller.defVertexAttributes();
        RenderEngine.controller.pointVertexAttributes();
    }
  
    @Override
    public void setMatrix(Matrix4f model){
        this.model = model;
    }

    @Override
    public void draw(){    
        RenderEngine.controller.setModel(model);       
        vbo.bind(GL_ARRAY_BUFFER);
        evbo.bind(GL_ELEMENT_ARRAY_BUFFER);
        RenderEngine.controller.defInstancedVertexAttributes(ivbo);
        RenderEngine.controller.setInstanced(true);
        glDrawArraysInstanced(GL_TRIANGLES,0,ind.limit(), instnum);
        RenderEngine.controller.setInstanced(false);
    }
    
    public void removeBuffer(){
        b = null;
        
    }

    @Override
    public Matrix4f getMatrix() {
        return model;
    }

    @Override
    public void destroy() {
        removeBuffer();
    }
    public void setPositions(FloatBuffer f,int numparticles){
        ivbo.bind(GL_ARRAY_BUFFER);
        ivbo.uploadData(GL_ARRAY_BUFFER, f, GL_STATIC_DRAW);
        instnum = numparticles;
    }
}
