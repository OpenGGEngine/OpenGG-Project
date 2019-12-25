package com.opengg.core.render.internal.opengl.texture;

import com.opengg.core.render.texture.Renderbuffer;

public class OpenGLRenderbuffer implements Renderbuffer{
    private final NativeOpenGLRenderbuffer nativebuffer;

    public OpenGLRenderbuffer(int x, int y, int internalformat){
        nativebuffer = new NativeOpenGLRenderbuffer();
        nativebuffer.bind();
        nativebuffer.createStorage(internalformat, x, y);
    }

    @Override
    public void bind(){
        nativebuffer.bind();
    }

    @Override
    public int getID(){
        return nativebuffer.getId();
    }

    @Override
    public void delete(){
        nativebuffer.delete();
    }
}
