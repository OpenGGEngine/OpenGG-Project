/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector3f;
import com.opengg.core.render.shader.ShaderHandler;
import com.opengg.core.util.GlobalInfo;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferData;

/**
 *
 * @author Javier
 */
public class DrawnObject {
    VertexBufferObject vbo;
    long offset;
    FloatBuffer b;
    IntBuffer ind;
    int limit;
    int vertLimit;
    long vertOffset;
    
    Matrix4f model = Matrix4f.translate(0, 0, 0);
    Matrix4f shadeModel = new Matrix4f();
    
    static{
        DrawnObjectHandler.setup();
    }
    private IntBuffer lineInd;
    
    public DrawnObject(FloatBuffer b, VertexBufferObject vbo2, int vertSize){
        limit = b.limit();
        offset = DrawnObjectHandler.getOffset();
        vertLimit = limit/vertSize;
        vertOffset = offset/vertSize;
        
        ind = BufferUtils.createIntBuffer(vertLimit);
        for(long i = vertOffset; i < vertLimit + vertOffset; i++){
            ind.put((int) i);
        }
        ind.flip();
        
        lineInd = BufferUtils.createIntBuffer(vertLimit*2);
        for(long i = vertOffset; i < vertLimit + vertOffset; i+=3){
            lineInd.put((int) i);
            lineInd.put((int) i+1);
            lineInd.put((int) i+1);
            lineInd.put((int) i+2);
            lineInd.put((int) i+2);
            lineInd.put((int) i);
        }
        lineInd.flip();
        
        this.b = b;
        vbo = vbo2;
        vbo.uploadSubData(GL_ARRAY_BUFFER, offset*4, b);
        DrawnObjectHandler.addToOffset(limit);
    }
    
    
    public DrawnObject(FloatBuffer b, VertexBufferObject vbo2, IntBuffer index){
        ind = BufferUtils.createIntBuffer(vertLimit);
        limit = b.limit();
        offset = DrawnObjectHandler.getOffset();
        vertLimit = limit/12;
        vertOffset = offset/12;
        
        for(int i = 0; i < index.limit(); i++){
            ind.put((int) (index.get(i)+vertOffset));
        }
        ind.flip();
        
        this.b = b;
        vbo = vbo2;
        vbo.uploadSubData(GL_ARRAY_BUFFER, offset*4, b);
        DrawnObjectHandler.addToOffset(limit);
    }
    public void draw(){
        
        ShaderHandler.setModel(model);

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ind, GL_STATIC_DRAW);
        glDrawElements(GL_TRIANGLES, ind.limit(), GL_UNSIGNED_INT, 0);       
    }
    
    public void drawShaded(){
        
        ShaderHandler.setModel(model);
        GlobalInfo.main.setShadowLightMatrix(shadeModel);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ind, GL_STATIC_DRAW);
        glDrawElements(GL_TRIANGLES, ind.limit(), GL_UNSIGNED_INT, 0);       
    }
    
    public void saveShadowMVP(){
        ShaderHandler.setModel(model);
        shadeModel = (ShaderHandler.getMVP());
    }
    
    public void setShaderMatrix(Matrix4f m){
        shadeModel = m;
    }
    
    public void setModel(Matrix4f model){
        this.model = model;
    }
    
    public void drawPoints(){
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ind, GL_STATIC_DRAW);
        glDrawElements(GL_POINTS, ind.limit(), GL_UNSIGNED_INT, 0);    
    }
    public void drawLines(){
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, lineInd, GL_STATIC_DRAW);
        glDrawElements(GL_LINES, ind.limit(), GL_UNSIGNED_INT, 0);    
    }
    public void removeBuffer(){
        b = null;
    }
}
