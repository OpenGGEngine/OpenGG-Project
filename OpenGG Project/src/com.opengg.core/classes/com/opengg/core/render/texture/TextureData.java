/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.texture;

import java.nio.Buffer;

/**
 *
 * @author Javier
 */
public class TextureData {
    public int width;
    public int height;
    public Buffer buffer;
    public String source;
    public boolean complete = false;
    
    public TextureData(int width, int height, Buffer buffer, String source) {
        this.width = width;
        this.height = height;
        this.buffer = buffer;
        this.source = source;
    }
    
    public void setComplete(){
        complete = true;
    }
}
