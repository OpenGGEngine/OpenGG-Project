/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.texture;

import com.opengg.core.engine.Resource;
import java.nio.Buffer;

/**
 *
 * @author Javier
 */
public class TextureData implements Resource{
    public int width;
    public int height;
    public int channels;
    public Buffer buffer;
    public String source;
    public boolean complete = false;
    
    public TextureData(int width, int height, int channels, Buffer buffer, String source) {
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.buffer = buffer;
        this.source = source;
    }
    
    public void setComplete(){
        complete = true;
    }

    @Override
    public Type getType() {
        return Type.TEXTURE;
    }

    @Override
    public String getSource() {
        return source;
    }
}
