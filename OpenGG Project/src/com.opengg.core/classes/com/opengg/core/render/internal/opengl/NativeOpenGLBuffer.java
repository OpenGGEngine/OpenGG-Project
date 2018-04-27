/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL15.GL_BUFFER_SIZE;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glGetBufferParameteri;
import static org.lwjgl.opengl.GL30.glBindBufferBase;

/**
 *
 * @author Javier
 */
public class NativeOpenGLBuffer{
    private final int id;
    
    /**
     * Creates a Vertex Buffer Object (VBO).
     */
    public NativeOpenGLBuffer() {
        id = glGenBuffers(); 
    }
    
    public void bind(int target) {
        glBindBuffer(target, id);
    }
    
    public void unbind(int target) {
        glBindBuffer(target, 0);
    }
    
    public void uploadData(int target, FloatBuffer data, int usage) {
        glBufferData(target, data, usage);
    }

    public void uploadData(int target, long size, int usage) {
        glBufferData(target, size, usage);
    }

    public void uploadSubData(int target, long offset, FloatBuffer data) {
        glBufferSubData(target, offset, data);
    }
    
    public void uploadSubData(int target, long offset, IntBuffer data) {
        glBufferSubData(target, offset, data);
    }

    public void uploadData(int target, IntBuffer data, int usage) {
        glBufferData(target, data, usage);
    }
    
    public void bindBase(int target, int base){
        glBindBufferBase(target, base, id);
    }

    public void delete() {
        glDeleteBuffers(id);
    }

    public int getSize(int target){
        return glGetBufferParameteri(target, GL_BUFFER_SIZE);
    }
    
    public int getID() {
        return id;
    }
}
