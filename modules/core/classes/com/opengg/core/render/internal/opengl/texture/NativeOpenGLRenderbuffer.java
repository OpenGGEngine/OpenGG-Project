/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.internal.opengl.texture;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.system.NativeResource;
import com.opengg.core.system.NativeResourceManager;

import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL45.glCreateRenderbuffers;
import static org.lwjgl.opengl.GL45.glNamedRenderbufferStorage;

/**
 *
 * @author Javier
 */
public class NativeOpenGLRenderbuffer implements NativeResource {
    private final int id;
    
    public NativeOpenGLRenderbuffer(){
        if(RenderEngine.validateInitialization()) id = -1;
        else id = glCreateRenderbuffers();
        NativeResourceManager.registerNativeResource(this);
    }

    public void createStorage(int internalformat, int width, int height){
        if(RenderEngine.validateInitialization()) return;
        glNamedRenderbufferStorage(id, internalformat, width, height);
    }

    public int getId(){
        return id;
    }

    public void delete(){
        if(RenderEngine.validateInitialization()) return;
        glDeleteRenderbuffers(id);
    }

    @Override
    public Runnable onDestroy() {
        int nid = id;
        return () -> glDeleteRenderbuffers((nid));
    }
}
