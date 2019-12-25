/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.opengg.core.render.texture;

import com.opengg.core.render.internal.opengl.texture.OpenGLRenderbuffer;

/**
 *
 * @author Javier
 */
public interface Renderbuffer{
    static Renderbuffer create(int x, int y, int storage){
        return new OpenGLRenderbuffer(x, y, storage);
    }

    void bind();

    int getID();

    void delete();
}
