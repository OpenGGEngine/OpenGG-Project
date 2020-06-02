/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.opengg.core.render.texture;

import com.opengg.core.engine.Resource;
import com.opengg.core.math.Vector2i;
import com.opengg.core.math.Vector3i;
import com.opengg.core.math.Vector4f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.internal.opengl.texture.OpenGLTexture;
import com.opengg.core.render.internal.vulkan.texture.VulkanImage;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.system.Allocator;
import com.opengg.core.util.GGOutputStream;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT;

/**
 * Represents an instance of a graphics API texture <br><br>
 * @author Javier
 */
public interface Texture{
    /**
     * Binds the texture to the current texture unit
     */
    void bind();

    /**
     * Unbinds the texture from the current texture unit
     */
    void unbind();

    /**
     * Sets the current texture unit
     * @param loc Texture unit
     */
    void setActiveTexture(int loc);

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
    int getID();

    static Texture get2DTexture(String path){
        return get2DTexture(Resource.getTextureData(path));
    }

    static Texture get2DTexture(TextureData data){
        return get2DTexture(data, SamplerFormat.RGBA, TextureFormat.SRGBA8, InputFormat.UNSIGNED_BYTE);
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

    static Texture get2DFramebufferTexture(int x, int y, SamplerFormat format, TextureFormat intformat, InputFormat input){
        TextureData data = new TextureData(x, y, 4, null, "framebuffer");

        Texture texture = new OpenGLTexture(Texture.config().samplerFormat(format).internalFormat(intformat).inputFormat(input)
                .wrapType(WrapType.CLAMP_BORDER).minimumFilter(FilterType.LINEAR).maxFilter(FilterType.LINEAR), new Vector3i(x,y,1));
        texture.bind();
        texture.set2DData(data);
        return texture;
    }

    static Texture getCubemapFramebufferTexture(int x, int y, SamplerFormat format, TextureFormat intformat, InputFormat input){
        TextureData data = new TextureData(x, y, 4, null, "framebuffer");
        Texture texture = new OpenGLTexture(Texture.cubemapConfig().samplerFormat(format).internalFormat(intformat).inputFormat(input)
                .wrapType(WrapType.CLAMP_BORDER).minimumFilter(FilterType.LINEAR).maxFilter(FilterType.NEAREST), new Vector3i(x,y,1));
        texture.bind();
        texture.setCubemapData(data, data, data, data, data, data);
        return texture;
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
        var tex = create(Texture.config(), TextureGenerator.ofColor(r,g,b,a));
        return tex;
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

    static Texture create(TextureConfig config, TextureData... data){
        if(data.length == 0) return Texture.create(config, TextureManager.getDefault());

        var size = switch (config.getType()){
            case TEXTURE_ARRAY -> {
                var x = Arrays.stream(data).mapToInt(d ->  d.width).max().getAsInt();
                var y = Arrays.stream(data).mapToInt(d ->  d.height).max().getAsInt();
                yield new Vector3i(x,y,data.length);
            }
            case TEXTURE_CUBEMAP, TEXTURE_2D -> new Vector3i(data[0].width, data[0].height, 1);
            case TEXTURE_3D -> new Vector3i(data[0].width, data[0].height, data.length);
        };

        if(config.getType() == TextureType.TEXTURE_ARRAY) {
            try {
                data = setSize(data,size.x,size.y);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        var texture = switch (RenderEngine.getRendererType()){
            case OPENGL -> new OpenGLTexture(config, size);
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

        if(RenderEngine.getRendererType() == WindowInfo.RendererType.OPENGL) texture.unbind();

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

    class TextureConfig{
        static final TextureConfig defaultconfig = new TextureConfig();

        final TextureType type;
        final FilterType minFilter;
        final FilterType maxFilter;
        final WrapType wrapType;
        final SamplerFormat samplerFormat;
        final TextureFormat internalFormat;
        final InputFormat inputFormat;
        final boolean anisotropic;

        public TextureConfig(){
            this(TextureType.TEXTURE_2D, FilterType.NEAREST, FilterType.NEAREST, WrapType.REPEAT, SamplerFormat.RGBA, TextureFormat.SRGBA8, InputFormat.UNSIGNED_BYTE, true);
        }

        public TextureConfig(TextureType type, FilterType minFilter, FilterType maxFilter, WrapType wrapType, SamplerFormat samplerFormat, TextureFormat internalFormat, InputFormat inputFormat, boolean anisotropic) {
            this.type = type;
            this.minFilter = minFilter;
            this.maxFilter = maxFilter;
            this.wrapType = wrapType;
            this.samplerFormat = samplerFormat;
            this.internalFormat = internalFormat;
            this.inputFormat = inputFormat;
            this.anisotropic = anisotropic;
        }

        public TextureConfig type(TextureType type) {
            return new TextureConfig(type, minFilter, maxFilter, wrapType, samplerFormat, internalFormat, inputFormat, anisotropic);
        }

        public TextureConfig minimumFilter(FilterType minfilter) {
            return new TextureConfig(type, minfilter, maxFilter, wrapType, samplerFormat, internalFormat, inputFormat, anisotropic);
        }

        public TextureConfig maxFilter(FilterType maxfilter) {
            return new TextureConfig(type, minFilter, maxfilter, wrapType, samplerFormat, internalFormat, inputFormat, anisotropic);
        }

        public TextureConfig wrapType(WrapType wraptype) {
            return new TextureConfig(type, minFilter, maxFilter, wraptype, samplerFormat, internalFormat, inputFormat, anisotropic);
        }

        public TextureConfig samplerFormat(SamplerFormat format) {
            return new TextureConfig(type, minFilter, maxFilter, wrapType, format, internalFormat, inputFormat, anisotropic);
        }

        public TextureConfig internalFormat(TextureFormat intformat) {
            return new TextureConfig(type, minFilter, maxFilter, wrapType, samplerFormat, intformat, inputFormat, anisotropic);
        }

        public TextureConfig inputFormat(InputFormat input) {
            return new TextureConfig(type, minFilter, maxFilter, wrapType, samplerFormat, internalFormat, input, anisotropic);
        }

        public TextureType getType() {
            return type;
        }

        public FilterType getMinFilter() {
            return minFilter;
        }

        public FilterType getMaxFilter() {
            return maxFilter;
        }

        public WrapType getWrapType() {
            return wrapType;
        }

        public SamplerFormat getSamplerFormat() {
            return samplerFormat;
        }

        public TextureFormat getInternalFormat() {
            return internalFormat;
        }

        public InputFormat getInputFormat() {
            return inputFormat;
        }

        public boolean isAnisotropic() {
            return anisotropic;
        }
    }

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
        DEPTH32_STENCIL8
    }
}
