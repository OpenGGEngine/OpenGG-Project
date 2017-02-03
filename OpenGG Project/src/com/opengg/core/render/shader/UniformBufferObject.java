/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Javier
 */
public class UniformBufferObject {
    int ubo;
    int index = -1;
    int size;
    
    public UniformBufferObject(int size){
        this.size = size;
        FloatBuffer f = MemoryUtil.memAllocFloat(size);
        for(int i = 0; i < size; i++){
            f.put(0);
        }
        f.flip();
        
        ubo = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, ubo);
        glBufferData(GL_UNIFORM_BUFFER, f, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
    
    public UniformBufferObject(FloatBuffer fb){
        ubo = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, ubo);
        glBufferData(GL_UNIFORM_BUFFER, fb, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
    
    public void setBufferBindIndex(int index){
        this.index = index;
        glBindBufferBase(GL_UNIFORM_BUFFER, index, ubo);
    }
    
    public void updateBuffer(FloatBuffer fb, int loc){
        glBindBuffer(GL_UNIFORM_BUFFER, ubo);
        glBufferSubData(GL_UNIFORM_BUFFER, loc, fb);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
}
