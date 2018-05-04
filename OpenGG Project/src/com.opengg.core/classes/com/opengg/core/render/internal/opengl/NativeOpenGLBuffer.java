/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl;

import com.opengg.core.render.RenderEngine;

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
        if(!RenderEngine.validateInitialization()) id = -1;
        else id = glGenBuffers();
    }
    
    public void bind(int target) {
        if(!RenderEngine.validateInitialization()) return;
        glBindBuffer(target, id);
    }
    
    public void unbind(int target) {
        if(!RenderEngine.validateInitialization()) return;
        glBindBuffer(target, 0);
    }
    
    public void uploadData(int target, FloatBuffer data, int usage) {
        if(!RenderEngine.validateInitialization()) return;
        glBufferData(target, data, usage);
    }

    public void uploadData(int target, long size, int usage) {
        if(!RenderEngine.validateInitialization()) return;
        glBufferData(target, size, usage);
    }

    public void uploadSubData(int target, long offset, FloatBuffer data) {
        if(!RenderEngine.validateInitialization()) return;
        glBufferSubData(target, offset, data);
    }
    
    public void uploadSubData(int target, long offset, IntBuffer data) {
        if(!RenderEngine.validateInitialization()) return;
        glBufferSubData(target, offset, data);
    }

    public void uploadData(int target, IntBuffer data, int usage) {
        if(!RenderEngine.validateInitialization()) return;
        glBufferData(target, data, usage);
    }
    
    public void bindBase(int target, int base){
        if(!RenderEngine.validateInitialization()) return;
        glBindBufferBase(target, base, id);
    }

    public void delete() {
        if(!RenderEngine.validateInitialization()) return;
        glDeleteBuffers(id);
    }

    public int getSize(int target){
        if(!RenderEngine.validateInitialization()) return -1;
        return glGetBufferParameteri(target, GL_BUFFER_SIZE);
    }
    
    public int getID() {
        return id;
    }
}
