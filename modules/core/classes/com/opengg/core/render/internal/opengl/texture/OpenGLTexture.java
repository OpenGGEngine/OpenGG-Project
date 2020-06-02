/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.internal.opengl.texture;

import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Vector2i;
import com.opengg.core.math.util.Tuple;
import com.opengg.core.math.Vector3i;
import com.opengg.core.math.Vector4f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.system.Allocator;
import java.nio.ByteBuffer;
import java.util.*;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;

import static java.util.Map.entry;
import static org.lwjgl.opengl.EXTTextureCompressionS3TC.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.KHRTextureCompressionASTCLDR.*;


/**
 *
 * @author Javier
 */
public class OpenGLTexture implements Texture {
    private NativeOpenGLTexture tex;
    private List<TextureData> tdata = new ArrayList<>();

    private int type;
    private int samplerFormat;
    private int internalFormat;
    private int inputFormat;

    private Vector3i size;
    private int layers;

    public OpenGLTexture(TextureConfig config, Vector3i size) {
        this.size = size;

        tex = new NativeOpenGLTexture();
        this.type = getOpenGLTextureType(config.getType());
        this.samplerFormat = getOpenGLSamplerFormat(config.getSamplerFormat());
        this.internalFormat = getOpenGLTextureFormat(config.getInternalFormat());
        this.inputFormat = getOpenGLInputFormat(config.getInputFormat());
        setActiveTexture(0);
        bind();
        setMinimumFilterType(getOpenGlFilter(config.getMinFilter()));
        setMaximumFilterType(getOpenGlFilter(config.getMaxFilter()));
        setTextureWrapType(getOpenGlWrapType(config.getWrapType()));
    }

    @Override
    public void bind(){
        tex.bind(type);
    }
    
    @Override
    public void unbind(){
        if(RenderEngine.validateInitialization()) return;
        glBindTexture(type, 0);
    }
    
    @Override
    public void setActiveTexture(int loc){
        tex.setActiveTexture(GL_TEXTURE0 + loc);
    }
    
    public void use(int loc){
        tex.setActiveTexture(GL_TEXTURE0 + loc);
        tex.bind(type);
    }

    @Override
    public void set2DData(TextureData data){
        switch (data.getTextureType()) {
            case NORMAL -> tex.setImageData(type, 0, internalFormat, data.width, data.height, 0, samplerFormat, inputFormat, (ByteBuffer) data.buffer);
            case ATSC -> tex.setImageDataCompressed(type, 0, selectASTCFormat(data.getXBlock(), data.getYBlock(), true), data.width, data.height, 0, (ByteBuffer) data.buffer);
            case DXT1, DXT3, DXT5 -> {
                int blockSize = (data.getTextureType() == TextureData.TextureDataType.DXT1) ? 8 : 16;
                int width = data.width;
                int height = data.height;
                int internalFormat = switch (data.getTextureType()) {
                    case DXT1 -> GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
                    case DXT3 -> GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
                    case DXT5 -> GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
                    default -> GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
                };
                for (int level = 0; level < data.getMipMapCount(); level++) {
                    int size = ((data.width + 3) / 4) * ((data.height + 3) / 4) * blockSize;
                    byte[] sub = new byte[size];
                    ((ByteBuffer) data.buffer).get(sub);
                    ByteBuffer subBuffer = Allocator.alloc(size).put(sub).flip();
                    tex.setImageDataCompressed(type, level, internalFormat, width, height, 0, subBuffer);
                    width /= 2;
                    height /= 2;
                }
            }
        }
        tdata.add(data);
    }
    
    @Override
    public void set2DSubData(TextureData data, Vector2i offset){
        tex.setSubImageData(type, 0, offset.x, offset.y, data.width, data.height, samplerFormat, inputFormat, (ByteBuffer)data.buffer);
    }
    
    @Override
    public void setCubemapData(TextureData data1, TextureData data2, TextureData data3, TextureData data4, TextureData data5, TextureData data6){
        tex.setImageData(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, internalFormat, data1.width, data1.height, 0, samplerFormat, inputFormat, (ByteBuffer)data1.buffer);
        tex.setImageData(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, internalFormat, data2.width, data2.height, 0, samplerFormat, inputFormat, (ByteBuffer)data2.buffer);
        tex.setImageData(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, internalFormat, data3.width, data3.height, 0, samplerFormat, inputFormat, (ByteBuffer)data4.buffer); //inverted to compensate for coordinate issues
        tex.setImageData(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, internalFormat, data4.width, data4.height, 0, samplerFormat, inputFormat, (ByteBuffer)data3.buffer);
        tex.setImageData(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, internalFormat, data5.width, data5.height, 0, samplerFormat, inputFormat, (ByteBuffer)data5.buffer);
        tex.setImageData(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, internalFormat, data6.width, data6.height, 0, samplerFormat, inputFormat, (ByteBuffer)data6.buffer);
        tdata.add(data1);
        tdata.add(data2);
        tdata.add(data3);
        tdata.add(data4);
        tdata.add(data5);
        tdata.add(data6);
    }
    
    @Override
    public void set3DData(TextureData[] datums){
        ByteBuffer full = get3DData(datums);
        full.flip();

        tex.setImageData(type, 0, internalFormat, datums[0].width, datums[0].height, datums.length, 0, samplerFormat, inputFormat, full);
        tdata.addAll(Arrays.asList(datums));
    }
    
    @Override
    public void set3DSubData(int xoffset, int yoffset, int zoffset, TextureData[] datums){
        get3DData(datums);
        ByteBuffer full = get3DData(datums);
        full.flip();
        tdata.addAll(Arrays.asList(datums));
        tex.setSubImageData(type, 0, xoffset, yoffset, zoffset, datums[0].width, datums[0].height, datums.length, GL_RGBA, inputFormat, full);
    }

    private ByteBuffer get3DData(TextureData[] datums) {
        long blength = 0;
        for (TextureData datum : datums) blength += datum.buffer.limit();
        ByteBuffer full = Allocator.alloc((int) blength);
        for(TextureData data : datums){
            full.put((ByteBuffer)data.buffer);
            data.buffer.rewind();
        }

        return full;
    }

    private void generateMipmaps(){
        tex.generateMipmap(type);
    }

    private void setMinimumLOD(int mlod){
        tex.setParameteri(type, GL_TEXTURE_MIN_LOD, mlod);
    }

    private void setMaximumLOD(int mlod){
        tex.setParameteri(type, GL_TEXTURE_MAX_LOD, mlod);
    }

    private void setMinimumFilterType(int ftype){
        tex.setParameteri(type, GL_TEXTURE_MIN_FILTER, ftype);
    }

    private void setMaximumFilterType(int ftype){
        tex.setParameteri(type, GL_TEXTURE_MAG_FILTER, ftype);
    }

    private void setAnisotropyLevel(int level){
        if(GL.getCapabilities().GL_EXT_texture_filter_anisotropic){
            float lev = Math.min(level, glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
            tex.setParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, lev);
        }else{
            GGConsole.warning("Anisotropy is not available on this device");
        }
    }

    private void setLODBias(int bias){
        tex.setParameteri(type, GL_TEXTURE_LOD_BIAS, bias);
    }

    private void setTextureWrapType(int wtype){
        tex.setParameteri(type, GL_TEXTURE_WRAP_S, wtype);
        tex.setParameteri(type, GL_TEXTURE_WRAP_T, wtype);
        tex.setParameteri(type, GL_TEXTURE_WRAP_R, wtype);
    }

    private void setBorderColor(Vector4f borderColor) {
        tex.setParameterfv(type, GL_TEXTURE_BORDER_COLOR, borderColor.toArray());
    }

    @Override
    public List<TextureData> getData(){
        return Collections.unmodifiableList(tdata);
    }
    
    @Override
    public int getID(){
        return tex.getID();
    }

    private Map<Tuple<Short,Short>,Integer> ASTCLookup =Map.ofEntries(entry(Tuple.of((short)10,(short)10),GL_COMPRESSED_RGBA_ASTC_10x10_KHR)
            ,entry(Tuple.of((short)10,(short)5),GL_COMPRESSED_RGBA_ASTC_10x5_KHR),entry(Tuple.of((short)10,(short)6),GL_COMPRESSED_RGBA_ASTC_10x6_KHR),
            entry(Tuple.of((short)10,(short)8),GL_COMPRESSED_RGBA_ASTC_10x8_KHR),entry(Tuple.of((short)12,(short)10),GL_COMPRESSED_RGBA_ASTC_12x10_KHR),
            entry(Tuple.of((short)12,(short)12),GL_COMPRESSED_RGBA_ASTC_12x12_KHR),entry(Tuple.of((short)4,(short)4),GL_COMPRESSED_RGBA_ASTC_4x4_KHR),
                    entry(Tuple.of((short)5,(short)4),GL_COMPRESSED_RGBA_ASTC_5x4_KHR),entry(Tuple.of((short)5,(short)5),GL_COMPRESSED_RGBA_ASTC_5x5_KHR),
                            entry(Tuple.of((short)6,(short)5),GL_COMPRESSED_RGBA_ASTC_6x5_KHR),entry(Tuple.of((short)6,(short)6),GL_COMPRESSED_RGBA_ASTC_6x6_KHR),
                                    entry(Tuple.of((short)8,(short)5),GL_COMPRESSED_RGBA_ASTC_8x5_KHR),entry(Tuple.of((short)8,(short)6),GL_COMPRESSED_RGBA_ASTC_8x6_KHR),
                                            entry(Tuple.of((short)8,(short)8),GL_COMPRESSED_RGBA_ASTC_8x8_KHR));
    private Map<Tuple<Short,Short>,Integer> SRGBASTCLookup =Map.ofEntries(entry(Tuple.of((short)10,(short)10),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x10_KHR)
            ,entry(Tuple.of((short)10,(short)5),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x5_KHR),entry(Tuple.of((short)10,(short)6),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x6_KHR),
            entry(Tuple.of((short)10,(short)8),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_10x8_KHR),entry(Tuple.of((short)12,(short)10),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_12x10_KHR),
            entry(Tuple.of((short)12,(short)12),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_12x12_KHR),entry(Tuple.of((short)4,(short)4),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_4x4_KHR),
            entry(Tuple.of((short)5,(short)4),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_5x4_KHR),entry(Tuple.of((short)5,(short)5),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_5x5_KHR),
            entry(Tuple.of((short)6,(short)5),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_6x5_KHR),entry(Tuple.of((short)6,(short)6),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_6x6_KHR),
            entry(Tuple.of((short)8,(short)5),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x5_KHR),entry(Tuple.of((short)8,(short)6),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x6_KHR),
            entry(Tuple.of((short)8,(short)8),GL_COMPRESSED_SRGB8_ALPHA8_ASTC_8x8_KHR));
    private int selectASTCFormat(short blockX,short blockY,boolean srgb){
        return srgb? SRGBASTCLookup.get(Tuple.of(blockX,blockY)):ASTCLookup.get(Tuple.of(blockX,blockY));
    }

    public static int getOpenGLTextureType(TextureType type){
        return switch (type){
            case TEXTURE_ARRAY -> GL_TEXTURE_2D_ARRAY;
            case TEXTURE_2D -> GL_TEXTURE_2D;
            case TEXTURE_3D -> GL_TEXTURE_3D;
            case TEXTURE_CUBEMAP -> GL_TEXTURE_CUBE_MAP;
        };
    }

    public static int getOpenGLTextureFormat(TextureFormat format){
        return switch (format){
            case RGB8 -> GL_RGB8;
            case RGBA8 -> GL_RGBA8;
            case SRGB8 -> GL_SRGB8;
            case SRGBA8 -> GL_SRGB8_ALPHA8;
            case RGB16 -> GL_RGB16;
            case RGBA16 -> GL_RGBA16;
            case RGBA16F -> GL_RGBA16F;
            case RGBA32F -> GL_RGBA32F;
            case RGB32F -> GL_RGB32F;
            case DEPTH32 -> GL_DEPTH_COMPONENT32F;
            case DEPTH24_STENCIL8 -> GL_DEPTH24_STENCIL8;
            case DEPTH32_STENCIL8 -> GL_DEPTH32F_STENCIL8;
        };
    }

    public static int getOpenGLSamplerFormat(SamplerFormat format){
        return switch (format){
            case RGB -> GL_RGB;
            case RGBA -> GL_RGBA;
            case DEPTH -> GL_DEPTH_COMPONENT;
            case DEPTH_STENCIL -> GL_DEPTH_STENCIL;
        };
    }

    public static int getOpenGLInputFormat(InputFormat format){
        return switch (format){
            case UNSIGNED_BYTE -> GL_UNSIGNED_BYTE;
            case UNSIGNED_INT_24_8 -> GL_UNSIGNED_INT_24_8;
            case FLOAT -> GL_FLOAT;
        };
    }

    public static int getOpenGlWrapType(WrapType type){
        return switch (type) {
            case CLAMP_BORDER -> GL_CLAMP_TO_BORDER;
            case CLAMP_EDGE -> GL_CLAMP_TO_EDGE;
            case REPEAT -> GL_REPEAT;
            case REPEAT_MIRRORED -> GL_MIRRORED_REPEAT;
        };
    }

    public static int getOpenGlFilter(FilterType type){
        return switch (type) {
            case LINEAR -> GL_LINEAR;
            case NEAREST -> GL_NEAREST;
        };
    }
}
