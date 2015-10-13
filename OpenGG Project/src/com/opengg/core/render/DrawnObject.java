/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render;

import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;

/**
 *
 * @author Javier
 */
public class DrawnObject {
    VertexBufferObject vbo;
    long offset;
    FloatBuffer b;
    public DrawnObject(FloatBuffer b, VertexBufferObject vbo2){
        offset = DrawnObjectHandler.getOffset();
        this.b = b;
        vbo = vbo2;
        vbo2.uploadSubData(GL_ARRAY_BUFFER, offset * 4 , b);
        DrawnObjectHandler.addToOffset(b.limit());
    }
    public void draw(){
        System.out.println(offset);
        glDrawArrays(GL_TRIANGLES, (int)offset * 4 ,  b.limit());
        
    }
}
