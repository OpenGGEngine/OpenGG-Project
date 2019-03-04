/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.internal.opengl.texture;

import com.opengg.core.console.GGConsole;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.system.Allocator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LOD;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MIN_LOD;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;


/**
 *
 * @author Javier
 */
public class OpenGLTexture implements Texture {
    NativeOpenGLTexture tex;
    List<TextureData> tdata = new ArrayList<>();
    int type;
    int colorformat;
    int internalformat;
    int datatype;
    
    int x;
    int y;
    int z;
    
    int minfilter;
    int maxfilter;
    int minlod;
    int maxlod;
    boolean anisotropy;
    int lodbias;
    int texwrap;
    
    boolean storage;

    public OpenGLTexture(int type){
        this(type, GL_RGBA, GL_RGBA8, GL_UNSIGNED_BYTE);
    }
    
    public OpenGLTexture(int type, int colorformat, int internalformat, int datatype){
        tex = new NativeOpenGLTexture();
        this.type = type;
        this.colorformat = colorformat;
        this.internalformat = internalformat;
        this.datatype = datatype;
    }
    
    @Override
    public void bind(){
        tex.bind(type);
    }
    
    @Override
    public void unbind(){
        if(!RenderEngine.validateInitialization()) return;
        glBindTexture(type, 0);
    }
    
    @Override
    public void setActiveTexture(int loc){
        tex.setActiveTexture(GL_TEXTURE0 + loc);
    }
    
    @Override
    public void use(int loc){
        tex.setActiveTexture(GL_TEXTURE0 + loc);
        tex.bind(type);
    }
    
    @Override
    public void set2DStorage(int width, int height){
        tex.setImageStorage(type, 0, internalformat, width, height);
    }
    
    @Override
    public void set3DStorage(int width, int height, int depth){
        tex.setImageStorage(type, 4, internalformat, width, height, depth);
    }
    
    @Override
    public void set2DData(TextureData data){
        x = data.width;
        y = data.height;
        tex.setImageData(type, 0, internalformat, data.width, data.height, 0, colorformat, datatype, (ByteBuffer)data.buffer);
        tdata.add(data);
    }
    
    @Override
    public void set2DSubData(int xoffset, int yoffset, TextureData data){
        tex.setSubImageData(type, 0, xoffset, yoffset, data.width, data.height, colorformat, datatype, (ByteBuffer)data.buffer);
    }
    
    @Override
    public void setCubemapData(TextureData data1, TextureData data2, TextureData data3, TextureData data4, TextureData data5, TextureData data6){
        x = data1.width;
        y = data1.height;
        tex.setImageData(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, internalformat, data1.width, data1.height, 0, colorformat, datatype, (ByteBuffer)data1.buffer);
        tex.setImageData(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, internalformat, data2.width, data2.height, 0, colorformat, datatype, (ByteBuffer)data2.buffer);
        tex.setImageData(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, internalformat, data3.width, data3.height, 0, colorformat, datatype, (ByteBuffer)data3.buffer);
        tex.setImageData(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, internalformat, data4.width, data4.height, 0, colorformat, datatype, (ByteBuffer)data4.buffer);
        tex.setImageData(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, internalformat, data5.width, data5.height, 0, colorformat, datatype, (ByteBuffer)data5.buffer);
        tex.setImageData(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, internalformat, data6.width, data6.height, 0, colorformat, datatype, (ByteBuffer)data6.buffer);
        tdata.add(data1);
        tdata.add(data2);
        tdata.add(data3);
        tdata.add(data4);
        tdata.add(data5);
        tdata.add(data6);
    }
    
    @Override
    public void set3DData(TextureData[] datums){
        long blength = 0;
        for(int i = 0; i < datums.length; i++) blength += datums[i].buffer.limit();
        ByteBuffer full = Allocator.alloc((int) blength);
        for(TextureData data : datums) full.put((ByteBuffer)data.buffer);
        
        tex.setImageData(type, 0, internalformat, datums[0].width, datums[0].height, datums.length, 0, colorformat, datatype, full);
        tdata.addAll(Arrays.asList(datums));
    }
    
    @Override
    public void set3DSubData(int xoffset, int yoffset, int zoffset, TextureData[] datums){
        long blength = 0;
        for(int i = 0; i < datums.length; i++) blength += datums[i].buffer.limit();
        ByteBuffer full = Allocator.alloc((int) blength);
        for(TextureData data : datums){
            full.put((ByteBuffer)data.buffer);
            data.buffer.rewind();
        }
        full.flip();
        tdata.addAll(Arrays.asList(datums));
        tex.setSubImageData(type, 0, xoffset, yoffset, zoffset, datums[0].width, datums[0].height, datums.length, GL_RGBA, datatype, full);
    }
    
    @Override
    public void generateMipmaps(){
        tex.generateMipmap(type);
    }
    
    @Override
    public void setMinimumLOD(int mlod){
        tex.setParameteri(type, GL_TEXTURE_MIN_LOD, mlod);
    }
    
    @Override
    public void setMaximumLOD(int mlod){
        tex.setParameteri(type, GL_TEXTURE_MAX_LOD, mlod);
    }
    
    @Override
    public void setMinimumFilterType(int ftype){
        tex.setParameteri(type, GL_TEXTURE_MIN_FILTER, ftype);
    }
    
    @Override
    public void setMaximumFilterType(int ftype){
        tex.setParameteri(type, GL_TEXTURE_MAG_FILTER, ftype);
    }
    
    @Override
    public void setAnisotropyLevel(int level){
        if(GL.getCapabilities().GL_EXT_texture_filter_anisotropic){
            float lev = Math.min(level, glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
            tex.setParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, lev);
        }else{
            GGConsole.warning("Anisotropy is not available on this device");
        }
    }
    
    @Override
    public void setLODBias(int bias){
        tex.setParameteri(type, GL_TEXTURE_LOD_BIAS, bias);
    }
    
    @Override
    public void setTextureWrapType(int wtype){
        tex.setParameteri(type, GL_TEXTURE_WRAP_S, wtype);
        tex.setParameteri(type, GL_TEXTURE_WRAP_T, wtype);
        tex.setParameteri(type, GL_TEXTURE_WRAP_R, wtype);
    }
    
    @Override
    public List<TextureData> getData(){
        return Collections.unmodifiableList(tdata);
    }
    
    @Override
    public int getID(){
        return tex.getID();
    }
    
    @Override
    public void delete(){
        tex.delete();
    }

}
