/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.Matrix4f;
import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.util.GlobalInfo;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

/**
 *
 * @author Javier
 */
public class DrawnObject implements Drawable {
    VertexBufferObject vbo;
    VertexBufferObject evbo;
    FloatBuffer b;
    IntBuffer ind; 
    boolean hasmat;
    int limit;
    int vertLimit;
    long vertOffset;
    
    Matrix4f model = Matrix4f.translate(0, 0, 0);
    Matrix4f shadeModel = new Matrix4f();
    
    static{
        DrawnObjectHandler.setup();
    }
   
    public DrawnObject(FloatBuffer b, int vertSize){
       
        limit = b.limit();
        vertLimit = limit/vertSize;
        
        ind = MemoryUtil.memAllocInt(vertLimit);
        for(long i = vertOffset; i < vertLimit; i++){
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
    
    public DrawnObject(List<FloatBuffer> buffers, int vertSize){
      
        for(FloatBuffer b: buffers){
        
            limit = b.limit();
            vertLimit = limit/vertSize;

            ind = MemoryUtil.memAllocInt(vertLimit);
            for(long i = vertOffset; i < vertLimit; i++){
                ind.put((int) i);
            }
            ind.flip();

            this.b = b;
        }
        
        defBuffers(b, ind);
        removeBuffer();
    }
    
    public DrawnObject(FloatBuffer b,IntBuffer index){
        
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
        GlobalInfo.main.pointVertexAttributes();
    }
    
    private void defAttrib(){
        GlobalInfo.main.defVertexAttributes();
    }
    
    @Override
    public void saveShadowMVP(){
        GlobalInfo.main.setModel(model);
        shadeModel = (GlobalInfo.main.getMVP());
    }
    
    public void setShaderMatrix(Matrix4f m){
        shadeModel = m;
    }
    
    @Override
    public void setMatrix(Matrix4f model){
        this.model = model;
    }


    public void setBuffer(FloatBuffer b, int vertSize){
        limit = b.limit();
        vertLimit = limit/vertSize;
        
        ind = MemoryUtil.memAllocInt(vertLimit);
        for(long i = vertOffset; i < vertLimit; i++){
            ind.put((int) i);
        }
        ind.flip();
        
        this.b = b;
        defBuffers(b, ind);
        
        removeBuffer();
    }    
    
    @Override
    public void draw(){    
        GlobalInfo.main.setModel(model);  
        GlobalInfo.main.setShadowLightMatrix(shadeModel); 
        vbo.bind(GL_ARRAY_BUFFER);
        evbo.bind(GL_ELEMENT_ARRAY_BUFFER);
        defAttrib();
        pointAttrib();
        glDrawElements(GL_TRIANGLES, ind.limit(), GL_UNSIGNED_INT, 0);       
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
}
