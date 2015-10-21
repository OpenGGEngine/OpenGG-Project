/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
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
    
    static{
        DrawnObjectHandler.setup();
    }
    
    public DrawnObject(FloatBuffer b, VertexBufferObject vbo2){
        limit = b.limit();
        offset = DrawnObjectHandler.getOffset();
        vertLimit = limit/12;
        vertOffset = offset/12;
        
        ind = BufferUtils.createIntBuffer(vertLimit);
        for(long i = vertOffset; i < vertLimit + vertOffset; i++){
            ind.put((int) i);
        }
        ind.flip();
        
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

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ind, GL_STATIC_DRAW);
        glDrawElements(GL_TRIANGLES, ind.limit(), GL_UNSIGNED_INT, 0);

        
    }
    public void removeBuffer(){
        b = null;
    }
}
