/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.texture;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author Javier
 */
public class TextureBufferGenerator {
    public static TextureData getFastBuffer(String path){
        return getFastBuffer(path,false);
    }
    
    public static TextureData getFastBuffer(String path, boolean flip){
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer w = stack.callocInt(1);
            IntBuffer h = stack.callocInt(1);
            IntBuffer comp = stack.callocInt(1);
            
            stbi_set_flip_vertically_on_load(flip);
            ByteBuffer image = stbi_load(path, w, h, comp, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load texture!"
                        + System.lineSeparator() + stbi_failure_reason());
            }
            TextureData data = new TextureData(w.get(), h.get(), image);
            return data;
        }
    }
}
