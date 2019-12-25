/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.render.internal.opengl.shader.NativeOpenGLPureVertexArrayObject;

/**
 *
 * @author Javier
 */
public interface PureVertexArrayObject{
    static PureVertexArrayObject create(){
        return new NativeOpenGLPureVertexArrayObject();
    }

    void bind();

    void delete();

    int getID();

    void unbind();
}
