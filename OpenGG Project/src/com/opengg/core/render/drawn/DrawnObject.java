/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.Matrix4f;
import com.opengg.core.io.newobjloader.Material;
import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.util.GlobalInfo;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBufferData;

/**
 *
 * @author Javier
 */
public class DrawnObject implements Drawable {
    VertexBufferObject vbo;
    long offset;
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
    private IntBuffer lineInd;
   
    public DrawnObject(FloatBuffer b, int vertSize){
       
        limit = b.limit();
        offset = DrawnObjectHandler.getOffset();
        vertLimit = limit/vertSize;
        vertOffset = offset/vertSize;
        
        ind = BufferUtils.createIntBuffer(vertLimit);
        for(long i = vertOffset; i < vertLimit; i++){
            ind.put((int) i);
        }
        ind.flip();
        
        this.b = b;
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);
        GlobalInfo.main.defVertexAttributes();
        vbo.uploadData(GL_ARRAY_BUFFER, b, GL_STATIC_DRAW);
        removeBuffer();
    }
    public DrawnObject(FloatBuffer b){
        this(b, 12);
    }
    
    public DrawnObject(List<FloatBuffer> buffers, VertexBufferObject vbo2, int vertSize){
      
        for(FloatBuffer b: buffers){
        
            limit = b.limit();
            offset = DrawnObjectHandler.getOffset();
            vertLimit = limit/vertSize;
            vertOffset = offset/vertSize;

            ind = BufferUtils.createIntBuffer(vertLimit);
            for(long i = vertOffset; i < vertLimit; i++){
                ind.put((int) i);
            }
            ind.flip();

            this.b = b;
            vbo = new VertexBufferObject();
            vbo.bind(GL_ARRAY_BUFFER);
            GlobalInfo.main.defVertexAttributes();
            vbo.uploadData(GL_ARRAY_BUFFER, b, GL_STATIC_DRAW);
        }
        removeBuffer();
    }
    
    public DrawnObject(FloatBuffer b, VertexBufferObject vbo2, IntBuffer index){
        
        limit = b.limit();
        offset = DrawnObjectHandler.getOffset();
        vertLimit = limit/12;
        vertOffset = offset/12;
        ind = BufferUtils.createIntBuffer(index.capacity());
        for(int i = 0; i < index.limit(); i++){
            ind.put((int) (index.get(i)));
        }
        ind.flip();
        
        this.b = b;
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);
        GlobalInfo.main.defVertexAttributes();
        vbo.uploadData(GL_ARRAY_BUFFER, b, GL_STATIC_DRAW);
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
    
    @Override
    public void drawPoints(){
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ind, GL_STATIC_DRAW);
        glDrawElements(GL_POINTS, ind.limit(), GL_UNSIGNED_INT, 0);    
    }
    public void drawLines(){
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, lineInd, GL_STATIC_DRAW);
        glDrawElements(GL_LINES, ind.limit(), GL_UNSIGNED_INT, 0);    
    }
        
    @Override
    public void draw(){    
        GlobalInfo.main.setModel(model);       
        vbo.bind(GL_ARRAY_BUFFER);
        GlobalInfo.main.defVertexAttributes();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ind, GL_STATIC_DRAW);
        glDrawElements(GL_TRIANGLES, ind.limit(), GL_UNSIGNED_INT, 0);       
        //glDrawArrays(GL_TRIANGLES, 0, ind.limit());
    }
    
    @Override
    public void drawShaded(){
        GlobalInfo.main.setModel(model);
        GlobalInfo.main.setShadowLightMatrix(shadeModel);
        if (!hasmat) GlobalInfo.main.passMaterial(Material.defaultmaterial,false,false);
        vbo.bind(GL_ARRAY_BUFFER);    
        GlobalInfo.main.defVertexAttributes();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ind, GL_STATIC_DRAW);
        glDrawElements(GL_TRIANGLES, ind.limit(), GL_UNSIGNED_INT, 0);
        //glDrawArrays(GL_TRIANGLES, 0, ind.limit());
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
