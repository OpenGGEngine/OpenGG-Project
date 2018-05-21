/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.opengg.core.render.texture;

import com.opengg.core.engine.Resource;
import com.opengg.core.render.internal.opengl.texture.OpenGLTexture;
import org.lwjgl.opengl.GL11;

import java.security.InvalidParameterException;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL21.GL_SRGB_ALPHA;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

/**
 *
 * @author Javier
 */
public interface Texture {


    void bind();

    void unbind();

    void setActiveTexture(int loc);

    void use(int loc);

    void set2DStorage(int width, int height);

    void set3DStorage(int width, int height, int depth);

    void set2DData(TextureData data);

    void set2DSubData(int xoffset, int yoffset, TextureData data);

    void setCubemapData(TextureData data1, TextureData data2, TextureData data3, TextureData data4, TextureData data5, TextureData data6);

    void set3DData(TextureData[] datums);

    void set3DSubData(int xoffset, int yoffset, int zoffset, TextureData[] datums);

    void generateMipmaps();

    void setMinimumLOD(int mlod);

    void setMaximumLOD(int mlod);

    void setMinimumFilterType(int ftype);

    void setMaximumFilterType(int ftype);

    void setAnisotropyLevel(int level);

    void setLODBias(int bias);

    void setTextureWrapType(int wtype);

    List<TextureData> getData();

    int getID();

    void delete();

    public static Texture get2DTexture(String path){
        return get2DTexture(TextureManager.loadTexture(path));
    }

    public static Texture get2DTexture(TextureData data){
        return get2DTexture(data, GL_RGBA, GL_RGBA8, GL_UNSIGNED_BYTE);
    }

    public static Texture get2DSRGBTexture(String path){
        return Resource.getSRGBTexture(path);
    }

    public static Texture get2DSRGBTexture(TextureData data){
        return get2DTexture(data, GL_RGBA, GL_SRGB_ALPHA, GL_UNSIGNED_BYTE);
    }

    public static Texture get2DTexture(TextureData data, int format, int intformat, int storage){
        data.buffer.rewind();
        Texture texture = new OpenGLTexture(GL_TEXTURE_2D, format, intformat, storage);
        texture.setActiveTexture(0);
        texture.bind();
        texture.set2DData(data);
        texture.setTextureWrapType(GL_REPEAT);
        texture.setMinimumFilterType(GL_LINEAR);
        texture.setMaximumFilterType(GL_NEAREST);
        texture.generateMipmaps();
        texture.unbind();
        return texture;
    }

    public static Texture get2DFramebufferTexture(int x, int y, int format, int intformat, int input){
        TextureData data = new TextureData(x, y, 4, null, "framebuffer");

        Texture texture = new OpenGLTexture(GL_TEXTURE_2D, format, intformat, input);
        texture.bind();
        texture.set2DData(data);
        texture.setTextureWrapType(GL_CLAMP);
        texture.setMinimumFilterType(GL_LINEAR);
        texture.setMaximumFilterType(GL_NEAREST);
        return texture;
    }

    public static Texture getCubemap(String path1, String path2, String path3, String path4, String path5, String path6){
        TextureData data1 = TextureManager.loadTexture(path1, false);
        TextureData data2 = TextureManager.loadTexture(path2, false);
        TextureData data3 = TextureManager.loadTexture(path3, false);
        TextureData data4 = TextureManager.loadTexture(path4, false);
        TextureData data5 = TextureManager.loadTexture(path5, false);
        TextureData data6 = TextureManager.loadTexture(path6, false);
        return getCubemap(data1,data2,data3,data4,data5,data6);
    }

    public static Texture getCubemap(TextureData data1, TextureData data2, TextureData data3, TextureData data4, TextureData data5, TextureData data6){
        return getCubemap(data1, data2, data3, data4, data5, data6, GL_RGBA, GL_RGBA8, GL_UNSIGNED_BYTE);
    }

    public static Texture getSRGBCubemap(String path1, String path2, String path3, String path4, String path5, String path6){
        TextureData data1 = TextureManager.loadTexture(path1, false);
        TextureData data2 = TextureManager.loadTexture(path2, false);
        TextureData data3 = TextureManager.loadTexture(path3, false);
        TextureData data4 = TextureManager.loadTexture(path4, false);
        TextureData data5 = TextureManager.loadTexture(path5, false);
        TextureData data6 = TextureManager.loadTexture(path6, false);
        return getSRGBCubemap(data1,data2,data3,data4,data5,data6);
    }

    public static Texture getSRGBCubemap(TextureData data1, TextureData data2, TextureData data3, TextureData data4, TextureData data5, TextureData data6){
        return getCubemap(data1, data2, data3, data4, data5, data6, GL_RGBA, GL_SRGB_ALPHA, GL_UNSIGNED_BYTE);
    }

    public static Texture getCubemap(TextureData data1, TextureData data2, TextureData data3, TextureData data4, TextureData data5, TextureData data6, int format, int intformat, int storage){
        Texture texture = new OpenGLTexture(GL_TEXTURE_CUBE_MAP, format, intformat, storage);
        texture.setActiveTexture(0);
        texture.bind();
        texture.setCubemapData(data1, data2, data3, data4, data5, data6);
        texture.setTextureWrapType(GL_REPEAT);
        texture.setMinimumFilterType(GL_LINEAR);
        texture.setMaximumFilterType(GL_NEAREST);
        texture.generateMipmaps();
        texture.unbind();
        return texture;
    }

    public static Texture getArrayTexture(String... paths){
        TextureData[] datums = new TextureData[paths.length];
        for(int i = 0; i < paths.length; i++) datums[i] = TextureManager.loadTexture(paths[i]);
        return  getArrayTexture(datums);
    }

    public static Texture getArrayTexture(TextureData... datums){
        return getArrayTexture(GL_RGBA, GL_RGBA8, GL_UNSIGNED_BYTE, datums);
    }

    public static Texture getSRGBArrayTexture(String... paths){
        TextureData[] datums = new TextureData[paths.length];
        for(int i = 0; i < paths.length; i++) datums[i] = TextureManager.loadTexture(paths[i]);
        return  getSRGBArrayTexture(datums);
    }

    public static Texture getSRGBArrayTexture(TextureData... datums){
        return getArrayTexture(GL_RGBA, GL_SRGB_ALPHA, GL_UNSIGNED_BYTE, datums);
    }

    public static Texture getArrayTexture(int format, int intformat, int storage, TextureData... datums){
        Texture texture = new OpenGLTexture(GL_TEXTURE_2D_ARRAY, format, intformat, storage);
        texture.setActiveTexture(0);
        texture.bind();
        texture.set3DStorage(datums[0].width, datums[0].height, datums.length);
        texture.set3DSubData(0, 0, 0, datums);
        texture.setTextureWrapType(GL_REPEAT);
        texture.setMinimumFilterType(GL_LINEAR);
        texture.setMaximumFilterType(GL_LINEAR);
        texture.generateMipmaps();
        texture.unbind();
        return texture;
    }

    public static Texture create(TextureConfig config, String... data){
        TextureData[] textures = new TextureData[data.length];
        for(int i = 0; i < data.length; i++){
            textures[i] = Resource.getTextureData(data[i]);
        }
        return create(config, textures);
    }

    public static Texture create(TextureConfig config, TextureData... data){
        Texture texture = new OpenGLTexture(config.internaltype, config.format, config.intformat, config.input);
        texture.setActiveTexture(0);
        texture.bind();
        if(config.type == TextureType.TEXTURE_2D) {
            texture.set2DData(data[0]);

        }else if(config.type == TextureType.TEXTURE_ARRAY){
            texture.set3DStorage(data[0].width, data[0].height, data.length);
            texture.set3DSubData(0, 0, 0, data);

        }else if(config.type == TextureType.TEXTURE_CUBEMAP){
            if(data.length < 6) throw new InvalidParameterException("Incorrect amount of textures passed to cubemap generation  (expected 6, got " + data.length + ")");
            texture.setCubemapData(data[0], data[1], data[2], data[3], data[4], data[5]);
        }
        if(data != null) texture.generateMipmaps();

        texture.setTextureWrapType(config.wraptype);
        texture.setMinimumFilterType(config.minfilter);
        texture.setMaximumFilterType(config.maxfilter);
        texture.unbind();
        return texture;
    }

    public static TextureConfig config(){
        return TextureConfig.defaultconfig;
    }

    public static TextureConfig SRGBConfig(){
        return TextureConfig.defaultconfig.internalFormat(GL_SRGB_ALPHA);
    }

    public static TextureConfig arrayConfig(){
        return TextureConfig.defaultconfig.type(TextureType.TEXTURE_ARRAY);
    }

    public static TextureConfig arraySRGBConfig(){
        return TextureConfig.defaultconfig.internalFormat(GL_SRGB_ALPHA).type(TextureType.TEXTURE_ARRAY);
    }

    public static TextureConfig cubemapConfig(){
        return TextureConfig.defaultconfig.type(TextureType.TEXTURE_CUBEMAP);
    }

    public static TextureConfig cubemapSRGBConfig(){
        return TextureConfig.defaultconfig.internalFormat(GL_SRGB_ALPHA).type(TextureType.TEXTURE_CUBEMAP);
    }

    public static class TextureConfig{
        static final TextureConfig defaultconfig = new TextureConfig();

        final TextureType type;
        final int internaltype;
        final int minfilter;
        final int maxfilter;
        final int wraptype;
        final int format;
        final int intformat;
        final int input;
        final boolean anisotropic;

        public TextureConfig(){
            this(TextureType.TEXTURE_2D, GL_TEXTURE_2D, GL_LINEAR, GL_LINEAR, GL_REPEAT, GL_RGBA, GL_SRGB_ALPHA, GL_UNSIGNED_BYTE, false);
        }

        public TextureConfig(TextureType type, int internaltype, int minfilter, int maxfilter, int wraptype, int format, int intformat, int input, boolean anisotropic) {
            this.type = type;
            this.internaltype = internaltype;
            this.minfilter = minfilter;
            this.maxfilter = maxfilter;
            this.wraptype = wraptype;
            this.format = format;
            this.intformat = intformat;
            this.input = input;
            this.anisotropic = anisotropic;
        }

        public TextureConfig type(TextureType type) {
            int internaltype = 0;
            switch(type){
                case TEXTURE_2D:
                    internaltype = GL_TEXTURE_2D;
                    break;
                case TEXTURE_3D:
                    //s = GL_TEXTURE_3D;
                    break;
                case TEXTURE_ARRAY:
                    internaltype = GL_TEXTURE_2D_ARRAY;
                    break;
                case TEXTURE_CUBEMAP:
                    internaltype = GL_TEXTURE_CUBE_MAP;
                    break;
            }
            return new TextureConfig(type, internaltype, minfilter, maxfilter, wraptype, format, intformat, input, anisotropic);
        }

        public TextureConfig minimumFilter(int minfilter) {
            return new TextureConfig(type, internaltype, minfilter, maxfilter, wraptype, format, intformat, input, anisotropic);
        }

        public TextureConfig maxFilter(int maxfilter) {
            return new TextureConfig(type, internaltype, minfilter, maxfilter, wraptype, format, intformat, input, anisotropic);
        }

        public TextureConfig wrapType(int wraptype) {
            return new TextureConfig(type, internaltype, minfilter, maxfilter, wraptype, format, intformat, input, anisotropic);
        }

        public TextureConfig format(int format) {
            return new TextureConfig(type, internaltype, minfilter, maxfilter, wraptype, format, intformat, input, anisotropic);
        }

        public TextureConfig internalFormat(int intformat) {
            return new TextureConfig(type, internaltype, minfilter, maxfilter, wraptype, format, intformat, input, anisotropic);
        }

        public TextureConfig inputType(int input) {
            return new TextureConfig(type, internaltype, minfilter, maxfilter, wraptype, format, intformat, input, anisotropic);
        }
    }

    public enum TextureType{
        TEXTURE_ARRAY, TEXTURE_2D, TEXTURE_3D, TEXTURE_CUBEMAP
    }
}
