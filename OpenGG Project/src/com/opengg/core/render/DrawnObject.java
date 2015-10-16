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
    int offset;
    FloatBuffer b;
    int limit;
    public DrawnObject(FloatBuffer b, VertexBufferObject vbo2){
        limit = b.limit();
        offset = DrawnObjectHandler.getOffset();
        this.b = b;
        vbo = vbo2;
        vbo.uploadSubData(GL_ARRAY_BUFFER, offset * 8, b);
        DrawnObjectHandler.addToOffset(limit);
    }
    public void draw(){
            glDrawArrays(GL_TRIANGLES, offset * 8, limit);
        
    }
}
