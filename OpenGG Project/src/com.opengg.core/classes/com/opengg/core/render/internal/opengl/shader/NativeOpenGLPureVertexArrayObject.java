/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.internal.opengl.shader;

import com.opengg.core.render.shader.PureVertexArrayObject;

import static org.lwjgl.opengl.GL30.*;

/**
 *
 * @author Javier
 */
public class NativeOpenGLPureVertexArrayObject implements PureVertexArrayObject{

    /**
     * Stores the handle of the NativeOpenGLPureVertexArrayObject.
     */
    private final int id;

    /**
     * Creates a Vertex Array Object (VAO).
     */
    public NativeOpenGLPureVertexArrayObject() {
        id = glGenVertexArrays();
    }

    /**
     * Binds the NativeOpenGLPureVertexArrayObject.
     */
    @Override
    public void bind() {
        glBindVertexArray(id);
    }

    /**
     * Deletes the NativeOpenGLPureVertexArrayObject.
     */
    @Override
    public void delete() {
        glDeleteVertexArrays(id);
    }

    /**
     * Getter for the Vertex Array Object ID.
     *
     * @return Handle of the NativeOpenGLPureVertexArrayObject
     */
    @Override
    public int getID() {
        return id;
    }
    /**
     * Unbinds NativeOpenGLPureVertexArrayObject
     */
    @Override
    public void unbind(){
        glBindVertexArray(0);
    }
}

