/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.internal.opengl;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.system.NativeResource;
import com.opengg.core.system.NativeResourceManager;

import java.nio.ByteBuffer;
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
import static org.lwjgl.opengl.GL43.glBindVertexBuffer;
import static org.lwjgl.opengl.GL45.*;

/**
 *
 * @author Javier
 */
public class NativeOpenGLBuffer implements NativeResource {
    private final int id;
    
    /**
     * Creates a Vertex Buffer Object (VBO).
     */
    public NativeOpenGLBuffer() {
        if(RenderEngine.validateInitialization()) id = -1;
        else id = glCreateBuffers();
        NativeResourceManager.registerNativeResource(this);
    }

    public void setStorage(ByteBuffer data, int usage) {
        if(RenderEngine.validateInitialization()) return;
        glNamedBufferStorage(id, data, usage);
    }

    public void setStorage(FloatBuffer data, int usage) {
        if(RenderEngine.validateInitialization()) return;
        glNamedBufferStorage(id, data, usage);
    }

    public void setStorage(IntBuffer data, int usage) {
        if(RenderEngine.validateInitialization()) return;
        glNamedBufferStorage(id, data, usage);
    }

    public void createStorage(long size, int usage) {
        if(RenderEngine.validateInitialization()) return;
        glNamedBufferStorage(id, size, usage);
    }

    public void uploadSubData(long offset, FloatBuffer data) {
        if(RenderEngine.validateInitialization()) return;
        glNamedBufferSubData(id, offset, data);
    }

    public void uploadSubData(long offset, IntBuffer data) {
        if(RenderEngine.validateInitialization()) return;
        glNamedBufferSubData(id, offset, data);
    }

    public void uploadSubData(long offset, ByteBuffer data) {
        if(RenderEngine.validateInitialization()) return;
        glNamedBufferSubData(id, offset, data);
    }
    
    public void bindBase(int target, int base){
        if(RenderEngine.validateInitialization()) return;
        glBindBufferBase(target, base, id);
    }

    public void delete() {
        if(RenderEngine.validateInitialization()) return;
        glDeleteBuffers(id);
    }

    public int getSize(){
        if(RenderEngine.validateInitialization()) return -1;
        return glGetNamedBufferParameteri(id, GL_BUFFER_SIZE);
    }
    
    public int getID() {
        return id;
    }

    @Override
    public Runnable onDestroy() {
        int id2 = id;
        return () -> glDeleteBuffers(id2);
    }
}
