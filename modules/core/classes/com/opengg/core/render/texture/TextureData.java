/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.texture;

import com.opengg.core.engine.Resource;
import java.nio.Buffer;

/**
 * Container for data required to create a {@link Texture}. This class contains only client side representations, and is therefore thread safe
 * @author Javier
 */
public class TextureData implements Resource{
    //Only Used For ASTC compression
    private short xBlockSize,yBlockSize;

    private final TextureDataType type;
    /**
     * Width of texture
     */
    public final int width;

    /**
     * Height of texture
     */
    public final int height;

    /**
     * Amount of channels in texture
     */
    public final int channels;

    /**
     * Buffer containing texture data
     */
    public final Buffer buffer;

    /**
     * Source of texture (If texture comes from a file, this contains the file path)
     */
    public final String source;
    public boolean complete = false;

    public int mipMapCount;
    
    public TextureData(int width, int height, int channels, Buffer buffer, String source, TextureDataType type) {
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.buffer = buffer;
        this.source = source;
        this.type = type;
    }
    public TextureData(int width, int height, int channels, Buffer buffer, String source) {
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.buffer = buffer;
        this.source = source;
        this.type = TextureDataType.NORMAL;
    }
    
    public void setComplete(){
        complete = true;
    }

    @Override
    public Type getType() {
        return Type.TEXTURE;
    }

    public TextureDataType getTextureType(){return type;}

    @Override
    public String getSource() {
        return source;
    }

    //Only Used For ASTC textures
    public void setBlockSize(int x,int y){
        xBlockSize = (short)x;
        yBlockSize = (short)y;
    }
    public short getXBlock(){
        return xBlockSize;
    }

    public short getYBlock(){
        return yBlockSize;
    }

    public int getMipMapCount() {
        return mipMapCount;
    }

    public void setMipMapCount(int mipMapCount) {
        this.mipMapCount = mipMapCount;
    }

    public enum TextureDataType {
        NORMAL,
        DXT1,
        DXT3,
        DXT5,
        ATSC
    }

}
