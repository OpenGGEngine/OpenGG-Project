/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.opengg.core.render.texture;

import com.opengg.core.engine.Resource;
import com.opengg.core.math.Vector2i;
import com.opengg.core.math.Vector3i;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.internal.opengl.texture.OpenGLTexture;
import com.opengg.core.render.internal.vulkan.texture.VulkanImage;
import com.opengg.core.system.Allocator;
import com.opengg.core.util.GGOutputStream;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Represents an instance of a graphics API texture <br><br>
 * @author Javier
 */
public interface Texture{

    void setAsUniform(String uniform);

    /**
     * Uploads the given {@link TextureData} object to the texture<br><br>
     * This method automatically creates the required storage using the size of the given object
     * @param data TextureData to create a texture from
     */
    void set2DData(TextureData data);

    /**
     * Uploads the given {@link TextureData} object to the texture, starting at the given x and y offsets <br><br>
     * This method copies the data from the TextureData object from {@code xoffset, yoffset} to {@code xoffset + data.width, yoffset + data.height}
     * @param data TextureData to upload starting at the given offsets
     */
    void set2DSubData(TextureData data, Vector2i offset);

    void setCubemapData(TextureData data1, TextureData data2, TextureData data3, TextureData data4, TextureData data5, TextureData data6);

    void set3DData(TextureData[] datums);

    void set3DSubData(int xoffset, int yoffset, int zoffset, TextureData[] datums);

    /**
     * Returns all TextureData objets used to create this texture
     * @return
     */
    List<TextureData> getData();

    /**
     * Returns the texture ID for the underlying OpenGL texture
     * @return
     */
    long getID();

    static Texture get2DTexture(String path){
        return get2DTexture(Resource.getTextureData(path));
    }

    static Texture get2DTexture(TextureData data){
        return get2DTexture(data, SamplerFormat.RGBA, TextureFormat.RGBA8, InputFormat.UNSIGNED_BYTE);
    }

    static Texture get2DSRGBTexture(String path){
        return Resource.getSRGBTexture(path);
    }

    static Texture get2DSRGBTexture(TextureData data){
        return get2DTexture(data, SamplerFormat.RGBA, TextureFormat.SRGBA8, InputFormat.UNSIGNED_BYTE);
    }

    static Texture get2DTexture(TextureData data, SamplerFormat format, TextureFormat intformat, InputFormat storage){
        return create(Texture.config().samplerFormat(format).internalFormat(intformat).inputFormat(storage), data);
    }

    static Texture getSRGBCubemap(String path1, String path2, String path3, String path4, String path5, String path6){
        TextureData data1 = Resource.getTextureData(path1);
        TextureData data2 = Resource.getTextureData(path2);
        TextureData data3 = Resource.getTextureData(path3);
        TextureData data4 = Resource.getTextureData(path4);
        TextureData data5 = Resource.getTextureData(path5);
        TextureData data6 = Resource.getTextureData(path6);
        return getSRGBCubemap(data1,data2,data3,data4,data5,data6);
    }

    static Texture getSRGBCubemap(TextureData data1, TextureData data2, TextureData data3, TextureData data4, TextureData data5, TextureData data6){
        return getCubemap(data1, data2, data3, data4, data5, data6, SamplerFormat.RGBA, TextureFormat.SRGBA8, InputFormat.UNSIGNED_BYTE);
    }

    static Texture getCubemap(TextureData data1, TextureData data2, TextureData data3, TextureData data4, TextureData data5, TextureData data6, SamplerFormat format, TextureFormat intformat, InputFormat storage){
        return Texture.create(Texture.cubemapConfig().samplerFormat(format).internalFormat(intformat).inputFormat(storage), data1, data2, data3, data4, data5, data6);
    }

    static Texture ofColor(Color color){
        return ofColor(color, color.getTransparency());
    }

    static Texture ofColor(Color color, float transparency){
        return ofColor((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) (transparency*255f));
    }

    static Texture ofColor(byte r, byte g, byte b, byte a){
        return create(Texture.config(), TextureGenerator.ofColor(r,g,b,a));
    }

    static Texture create(TextureConfig config, String... data){
        TextureData[] textures = new TextureData[data.length];
        for(int i = 0; i < data.length; i++){
            textures[i] = Resource.getTextureData(data[i]);
        }

        return create(config, textures);
    }

    static Texture create(TextureConfig config, List<TextureData> data){
        return create(config, data.toArray(new TextureData[0]));
    }

    static Texture create(TextureConfig config, TextureData... data) {
        if (data.length == 0) return Texture.create(config, TextureManager.getDefault());

        config = config.internalFormat(
                switch (data[0].getTextureType()) {
                    case ATSC -> TextureFormat.ATSC;
                    case DXT1 -> TextureFormat.DXT1;
                    case DXT3 -> TextureFormat.DXT3;
                    case DXT5 -> TextureFormat.DXT5;
                    case NORMAL -> config.internalFormat;
                }
        );

        var size = switch (config.type()){
            case TEXTURE_ARRAY -> {
                var x = Arrays.stream(data).mapToInt(d ->  d.width).max().getAsInt();
                var y = Arrays.stream(data).mapToInt(d ->  d.height).max().getAsInt();
                yield new Vector3i(x,y,data.length);
            }
            case TEXTURE_CUBEMAP, TEXTURE_2D -> new Vector3i(data[0].width, data[0].height, 1);
            case TEXTURE_3D -> new Vector3i(data[0].width, data[0].height, data.length);
        };

        if(config.type() == TextureType.TEXTURE_ARRAY) {
            try {
                data = setSize(data,size.x,size.y);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        var storageProperties = new TextureStorageProperties(size, 1, data[0].getMipMapCount());

        if(data.length == 1 && data[0].getGPUTexture().isPresent()){
            return switch (RenderEngine.getRendererType()){
                case OPENGL -> new OpenGLTexture(config, size, (OpenGLTexture) data[0].getGPUTexture().get());
                case VULKAN -> throw new UnsupportedOperationException("Fix");
            };
        }

        var texture = switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLTexture(config, storageProperties);
            case VULKAN -> new VulkanImage(config, VK_SAMPLE_COUNT_1_BIT, VK_IMAGE_TILING_OPTIMAL, VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT, new Vector3i(size.x, size.y, 1), 6);
        };

        switch (config.type) {
            case TEXTURE_2D -> texture.set2DData(data[0]);
            case TEXTURE_ARRAY -> texture.set3DData(data);
            case TEXTURE_CUBEMAP -> {
                if (data.length < 6)
                    throw new InvalidParameterException("Incorrect amount of textures passed to cubemap generation  (expected 6, got " + data.length + ")");
                texture.setCubemapData(data[0], data[1], data[2], data[3], data[4], data[5]);
            }
        }

        if(data.length == 1){
            data[0].setGPUTexture(texture);
        }

        return texture;
    }

    private static TextureData[] setSize(TextureData[] input, int x, int y) throws IOException {
        ArrayList<TextureData> outList = new ArrayList<>();
        for(var data : input){
            var xMultiple = x/data.width;
            var yMultiple = y/data.height;

            var buffer = (ByteBuffer) data.buffer;

            GGOutputStream out = new GGOutputStream();
            for(int i = 0; i < data.height; i++){
                for(int i2 = 0; i2 < yMultiple; i2++){
                    for(int j = 0; j < data.width; j++){
                        for(int j2 = 0; j2 < xMultiple; j2++){
                            out.write(buffer.get((i * data.height + j) * 4 + 0));
                            out.write(buffer.get((i * data.height + j) * 4 + 1));
                            out.write(buffer.get((i * data.height + j) * 4 + 2));
                            out.write(buffer.get((i * data.height + j) * 4 + 3));
                        }
                    }
                }
            }

            var newBuffer = Allocator.alloc(out.asByteArray().length).put(out.asByteArray());
            newBuffer.flip();

            var newData = new TextureData(x,y,4,newBuffer,data.source);
            outList.add(newData);
        }

        return outList.toArray(new TextureData[0]);
    }

    static TextureConfig config(){
        return TextureConfig.defaultconfig;
    }

    static TextureConfig SRGBConfig(){
        return TextureConfig.defaultconfig.internalFormat(TextureFormat.SRGBA8);
    }

    static TextureConfig arrayConfig(){
        return TextureConfig.defaultconfig.type(TextureType.TEXTURE_ARRAY);
    }

    static TextureConfig arraySRGBConfig(){
        return TextureConfig.defaultconfig.internalFormat(TextureFormat.SRGBA8).type(TextureType.TEXTURE_ARRAY);
    }

    static TextureConfig cubemapConfig(){
        return TextureConfig.defaultconfig.type(TextureType.TEXTURE_CUBEMAP);
    }

    static TextureConfig cubemapSRGBConfig(){
        return TextureConfig.defaultconfig.internalFormat(TextureFormat.SRGBA8).type(TextureType.TEXTURE_CUBEMAP);
    }

    record TextureConfig(TextureType type, FilterType minFilter, FilterType maxFilter, WrapType wrapTypeS,WrapType wrapTypeT,WrapType wrapTypeR ,SamplerFormat samplerFormat, TextureFormat internalFormat, InputFormat inputFormat, boolean anisotropic){
        static final TextureConfig defaultconfig = new TextureConfig();


        public TextureConfig(){
            this(TextureType.TEXTURE_2D, FilterType.LINEAR, FilterType.LINEAR, WrapType.REPEAT,WrapType.REPEAT,WrapType.REPEAT, SamplerFormat.RGBA, TextureFormat.SRGBA8, InputFormat.UNSIGNED_BYTE,true);
        }

        public TextureConfig type(TextureType type) {
            return new TextureConfig(type, minFilter, maxFilter, wrapTypeS, wrapTypeT, wrapTypeR, samplerFormat, internalFormat, inputFormat, anisotropic);
        }

        public TextureConfig minimumFilter(FilterType minfilter) {
            return new TextureConfig(type, minfilter, maxFilter, wrapTypeS, wrapTypeT, wrapTypeR, samplerFormat, internalFormat, inputFormat, anisotropic);
        }

        public TextureConfig maxFilter(FilterType maxfilter) {
            return new TextureConfig(type, minFilter, maxfilter, wrapTypeS, wrapTypeT, wrapTypeR, samplerFormat, internalFormat, inputFormat, anisotropic);
        }

        public TextureConfig wrapType(WrapType wraptype) {
            return new TextureConfig(type, minFilter, maxFilter, wraptype,wraptype,wraptype, samplerFormat, internalFormat, inputFormat, anisotropic);
        }
        public TextureConfig wrapType(WrapType wraptypeS, WrapType wraptypeT, WrapType wraptypeR) {
            return new TextureConfig(type, minFilter, maxFilter, wraptypeS, wraptypeT, wraptypeR, samplerFormat, internalFormat, inputFormat, anisotropic);
        }

        public TextureConfig samplerFormat(SamplerFormat format) {
            return new TextureConfig(type, minFilter, maxFilter, wrapTypeS, wrapTypeT, wrapTypeR, format, internalFormat, inputFormat, anisotropic);
        }

        public TextureConfig internalFormat(TextureFormat intformat) {
            return new TextureConfig(type, minFilter, maxFilter, wrapTypeS, wrapTypeT, wrapTypeR, samplerFormat, intformat, inputFormat, anisotropic);
        }

        public TextureConfig inputFormat(InputFormat input) {
            return new TextureConfig(type, minFilter, maxFilter, wrapTypeS ,wrapTypeT, wrapTypeR, samplerFormat, internalFormat, input, anisotropic);
        }
    }

    record TextureStorageProperties(Vector3i size, int layers, int mipLevels){}

    enum FilterType{
        LINEAR, NEAREST
    }

    enum WrapType{
        CLAMP_BORDER,
        CLAMP_EDGE,
        REPEAT,
        REPEAT_MIRRORED
    }

    enum TextureType{
        TEXTURE_ARRAY,
        TEXTURE_2D,
        TEXTURE_3D,
        TEXTURE_CUBEMAP
    }

    enum InputFormat{
        UNSIGNED_BYTE,
        UNSIGNED_INT_24_8,
        FLOAT
    }

    enum SamplerFormat {
        RGB,
        RGBA,
        DEPTH,
        DEPTH_STENCIL
    }

    enum TextureFormat{
        RGB8,
        RGBA8,
        SRGB8,
        SRGBA8,
        RGB16,
        RGBA16,
        RGBA16F,
        RGBA32F,
        RGB32F,
        DEPTH32,
        DEPTH24_STENCIL8,
        DEPTH32_STENCIL8,
        DXT1,
        DXT3,
        DXT5,
        ATSC
    }
}
