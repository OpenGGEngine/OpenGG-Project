/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.internal.opengl.shader;

import com.opengg.core.render.RenderEngine;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL45.*;

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
        else id = glCreateVertexArrays();
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void enableAttribute(int attrib){
        glEnableVertexArrayAttrib(id, attrib);
    }

    public void setAttributeFormat(int attrib, int size, int type, boolean normalized, int offset){
        glVertexArrayAttribFormat(id, attrib, size, type, normalized, offset);
    }

    public void setAttributeBinding(int attrib,int bufferBinding){
        glVertexArrayAttribBinding(id, attrib, bufferBinding);
    }

    public void setBindingDivisor(int binding, int divisor){
        glVertexArrayBindingDivisor(id, binding, divisor);
    }

    public void applyVertexBufferToBinding(int bufferId, int binding, int vertexSize) {
        glVertexArrayVertexBuffer(id, binding, bufferId, 0, vertexSize);
    }

    public void applyElementBufferToBinding(int bufferId) {
        glVertexArrayElementBuffer(id, bufferId);
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



}

