/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.internal.opengl.shader;

import com.opengg.core.render.RenderEngine;

import static org.lwjgl.opengl.GL30.*;

/**
 *
 * @author Javier
 */
public class NativeOpenGLVertexArrayObject {

    /**
     * Stores the handle of the NativeOpenGLPureVertexArrayObject.
     */
    private final int id;

    /**
     * Creates a Vertex Array Object (VAO).
     */
    public NativeOpenGLVertexArrayObject() {
        if(RenderEngine.validateInitialization()) id = -1;
        else id = glGenVertexArrays();
    }

    /**
     * Binds the NativeOpenGLPureVertexArrayObject.
     */

    public void bind() {
        if(RenderEngine.validateInitialization()) return;
        glBindVertexArray(id);
    }

    /**
     * Deletes the NativeOpenGLPureVertexArrayObject.
     */

    public void delete(){
        if(RenderEngine.validateInitialization()) return;
        glDeleteVertexArrays(id);
    }

    /**
     * Getter for the Vertex Array Object ID.
     *
     * @return Handle of the NativeOpenGLPureVertexArrayObject
     */

    public int getID() {
        return id;
    }
    /**
     * Unbinds NativeOpenGLPureVertexArrayObject
     */

    public void unbind(){
        if(RenderEngine.validateInitialization()) return;
        glBindVertexArray(0);
    }
}

