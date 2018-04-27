/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.texture;

import com.opengg.core.engine.Resource;
import com.opengg.core.system.Allocator;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

/**
 *
 * @author Javier
 */
public class TextureLoader {
    public static TextureData loadTexture(String path) throws IOException{
        return loadTexture(path,true);
    }
    
    public static TextureData loadTexture(String path, boolean flip) throws IOException{
        IntBuffer w = Allocator.stackAllocInt(1);
        IntBuffer h = Allocator.stackAllocInt(1);
        IntBuffer comp = Allocator.stackAllocInt(1);

        stbi_set_flip_vertically_on_load(flip);

        String fpath = Resource.getAbsoluteFromLocal(path);

        ByteBuffer image = stbi_load(fpath, w, h, comp, 4);
        if (image == null) {
            Allocator.popStack();
            Allocator.popStack();
            Allocator.popStack();
            throw new IOException("Failed to load texture!");
        }

        Allocator.register(image, Allocator.LWJGL_DEFAULT);

        TextureData data = new TextureData(w.get(), h.get(), 4, image, path);
        Allocator.popStack();
        Allocator.popStack();
        Allocator.popStack();
        return data;
        
    }

    private TextureLoader() {
    }
}
