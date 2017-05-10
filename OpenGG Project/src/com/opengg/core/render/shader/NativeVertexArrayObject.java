/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.shader;

import static org.lwjgl.opengl.GL30.*;


public class NativeVertexArrayObject {

    /**
     * Stores the handle of the NativeVertexArrayObject.
     */
    private final int id;

    /**
     * Creates a Vertex Array Object (VAO).
     */
    public NativeVertexArrayObject() {
        id = glGenVertexArrays();
    }

    /**
     * Binds the NativeVertexArrayObject.
     */
    public void bind() {
        glBindVertexArray(id);
    }

    /**
     * Deletes the NativeVertexArrayObject.
     */
    public void delete() {
        glDeleteVertexArrays(id);
    }

    /**
     * Getter for the Vertex Array Object ID.
     *
     * @return Handle of the NativeVertexArrayObject
     */
    public int getID() {
        return id;
    }
    /**
     * Unbinds NativeVertexArrayObject
     */
    public void unbind(){
        glBindVertexArray(0);
    }
}

