/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.texture;

import com.opengg.core.engine.Resource;
import com.opengg.core.system.Allocator;
import org.lwjgl.stb.STBDXT;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

import static org.lwjgl.stb.STBDXT.STB_DXT_HIGHQUAL;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

/**
 *
 * @author Javier
 */
public class  TextureLoader {
    public static ByteBuffer compressDXT(String path) throws IOException {
        var data = loadTexture(path);

        ByteBuffer destination = Allocator.alloc(data.buffer.limit());
        STBDXT.stb_compress_dxt_block(destination,(ByteBuffer)data.buffer,true,STB_DXT_HIGHQUAL);
        return destination.slice();
    }
    public static TextureData loadTexture(String path) throws IOException{
        return loadTexture(path,true);
    }

    public static TextureData loadASTC(String path) throws IOException {
        File f = new File(path);
        ByteBuffer tex = Allocator.alloc((int) f.length());
        FileInputStream fIn = new FileInputStream(f);
        FileChannel fCh = fIn.getChannel();
        fCh.read(tex); tex.flip();
        //No joke this is what it is called in ASTC specs
        byte[] magic = new byte[4]; tex.get(magic);
        short xDim=tex.get(),yDim=tex.get(),ZDim=tex.get();
        byte[] xSize = new byte[3]; byte[] ySize = new byte[3]; byte[] zSize = new byte[3];
        tex.get(xSize).get(ySize).get(zSize);
        int texWidth = convertToShort(xSize); int texHeight = convertToShort(ySize); int texDepth = convertToShort(zSize);
        ByteBuffer dataBuffer = Allocator.alloc(tex.remaining());
        byte[] tempCopy = new byte[tex.remaining()];
        tex.get(tempCopy); dataBuffer.put(tempCopy).flip();
        TextureData data = new TextureData(texWidth,texHeight,4,dataBuffer,path, TextureData.TextureDataType.ATSC);
        data.setBlockSize(xDim,yDim);
        fIn.close();
        return data;
    }

    public static TextureData loadNormal(String path, boolean flip) throws IOException{
        IntBuffer w = Allocator.stackAllocInt(1);
        IntBuffer h = Allocator.stackAllocInt(1);
        IntBuffer comp = Allocator.stackAllocInt(1);

        stbi_set_flip_vertically_on_load(flip);

        String fpath = Resource.getAbsoluteFromLocal(path);

        fpath = fpath.replace(".tif", ".png");

        ByteBuffer image = stbi_load(fpath, w, h, comp, 4);
        if (image == null) {
            Allocator.popStack();
            Allocator.popStack();
            Allocator.popStack();
            throw new IOException("Failed to load texture!");
        }

        Allocator.register(image, Allocator.AllocType.NATIVE_HEAP);

        TextureData data = new TextureData(w.get(), h.get(), 4, image, path);
        Allocator.popStack();
        Allocator.popStack();
        Allocator.popStack();
        return data;
    }

    public static TextureData loadTexture(String path, boolean flip) throws IOException{
        if(path.contains(".astc")){
            return loadASTC(path);
        }else if(path.contains(".dds")) {
            return DDSLoader.load(path);
        }else {
            return loadNormal(path,flip);
        }
    }

    private TextureLoader() {
    }

    static int convertToShort(byte[] b) {
        return (((b[0] & 0xFF) << 0) | ((b[1] & 0xFF) << 8) | ((b[2] & 0xFF) << 16));
    }
}
