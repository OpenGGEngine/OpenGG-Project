/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.texture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author Javier
 */
public class TextureLoader {
    public static TextureData loadTexture(String path) throws IOException{
        return loadTexture(path,false);
    }
    
    public static TextureData loadTexture(String path, boolean flip) throws IOException{
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer w = stack.callocInt(1);
            IntBuffer h = stack.callocInt(1);
            IntBuffer comp = stack.callocInt(1);
            
            stbi_set_flip_vertically_on_load(flip);
            ByteBuffer image = stbi_load(path, w, h, comp, 4);
            if (image == null) {
                throw new IOException("Failed to load texture!");
            }
            TextureData data = new TextureData(w.get(), h.get(), image);
            return data;
        }
    }
}
