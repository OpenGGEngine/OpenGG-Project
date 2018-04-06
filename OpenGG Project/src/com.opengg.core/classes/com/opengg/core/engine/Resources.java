/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.audio.SoundData;
import com.opengg.core.audio.SoundManager;
import com.opengg.core.console.GGConsole;
import com.opengg.core.model.Model;
import com.opengg.core.model.ModelManager;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.text.GGFont;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class Resources {
    private static String current;
    
    public static void initialize(){
        ResourceManager.initialize();
        try{
            current = new File("").getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.error("Failed to get default path!");
        }
    }
    
    /**
     * Returns the absolute version of the path given a relative path, using the current directory of the OpenGG runtime as the starting point of the relative path. <br><br>
     * If the given path is already absolute, it will return the path unchanged
     * 
     * @param name Relative path to be converted
     * @return Absolute version of the path
     */
    public static String getAbsoluteFromLocal(String name){
        try {
            if(new File(name).isAbsolute()) return name;
            return new File(current, name).getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }
    
    public static boolean isAbsolute(String name){
        return new File(name).isAbsolute();
    }
    
    public static boolean exists(String name){
        return new File(name).exists();
    }
    
    private static String validate(String name){
        if(isAbsolute(name)) return name;
        if(exists(current + "\\"+ name)) return current + "\\"+ name;
        return null;
    }
    
    /**
     * Returns the path to a model with the given name, with the format resources/models/$name$/$name$.bmf
     * @param name Name of model, not including the .bmf extension
     * @return Relative path to the model
     */
    public static String getModelPath(String name){
        String source = validate(name);
        if(source != null) return source;
        return "resources" + File.separator + "models" + File.separator + name + File.separator + name + ".bmf";
    }
    
    /**
     * Returns the path to a configuration file with the given name, with the format cfg/$name$.cfg
     * @param name Name of configuration file, not including the .cfg extension
     * @return Relative path to the file
     */
    public static String getConfigPath(String name){
        String source = validate(name);
        if(source != null) return source;
        return "cfg" + File.separator + name + ".cfg";
    }
    
    /**
     * Returns the path to a shader file with the given name, with the format resources/glsl/$name$
     * @param name Name of shader file, including the respective file ending
     * @return Relative path to the shader file
     */
    public static String getShaderPath(String name){
        String source = validate(name);
        if(source != null) return source;
        return "resources" + File.separator + "glsl" + File.separator +  name;
    }
    
    /**
     * Returns the path to a texture with the given name, with the format resources/tex/$name$
     * @param name Name of texture, including the respective file ending
     * @return Relative path to the texture
     */
    public static String getTexturePath(String name){
        String source = validate(name);
        if(source != null) return source;
        return "resources" + File.separator + "tex" + File.separator +  name;
    }
    
    /**
     * Returns the path to a font file with the given name, with the format resources/font/$name$.fnt
     * @param name Name of font file, not including the .fnt extension
     * @return Relative path to the font file
     */
    public static String getFontPath(String name){
        String source = validate(name);
        if(source != null) return source;
        return "resources" + File.separator + "font" + File.separator +  name + ".fnt";
    }
    
    /**
     * Returns the path to a sound file with the given name, with the format resources/audio/$name$
     * @param name Name of sound, including the respective file ending
     * @return Relative path to the sound
     */
    public static String getSoundPath(String name){
        String source = validate(name);
        if(source != null) return source;
        return "resources" + File.separator + "audio" + File.separator +  name;
    }
    
    /**
     * Returns the path to a world with the given name, with the format resources/worlds/$name$.bwf
     * @param name Name of world, not including the .bwf extension
     * @return Relative path to the world file
     */
    public static String getWorldPath(String name){
        String source = validate(name);
        if(source != null) return source;
        return "resources" + File.separator + "worlds" + File.separator +  name + ".bwf";
    }
    
    /**
     * Returns the {@link com.opengg.core.audio.SoundData} with the given name <br>
     * It is acquired by calling {@link #getSoundPath(java.lang.String) } and passing the result into {@link com.opengg.core.audio.SoundManager#loadSound(java.lang.String) }
     * @param name Name of sound file to be loaded
     * @return SoundData object loaded from file
     */
    public static SoundData getSoundData(String name){
        return (SoundData) ResourceManager.prefetch(new ResourceRequest(getSoundPath(name), ResourceRequest.SOUND, 1)).get();
    }
    
    /**
     * Returns the {@link com.opengg.core.model.Model} with the given name <br>
     * It is acquired by calling {@link #getModelPath(java.lang.String) } and passing the result into {@link com.opengg.core.model.ModelManager#loadModel(java.lang.String) }
     * @param name Name of model file to be loaded
     * @return Model object loaded from file
     */
    public static Model getModel(String name){
        return (Model) ResourceManager.prefetch(new ResourceRequest(getModelPath(name), ResourceRequest.MODEL, 1)).get();
    }
    
    /**
     * Returns the {@link com.opengg.core.texture.Texture} with the given name <br>
     * It is acquired by calling {@link #getTexturePath(java.lang.String) } and passing the result into {@link com.opengg.core.render.texture.Texture#get2DTexture(java.lang.String) },
     * @param name Name of texture file to be loaded
     * @return Texture object loaded from file
     */
    public static Texture getTexture(String name){
        return Texture.get2DTexture((TextureData) ResourceManager.prefetch(new ResourceRequest(getTexturePath(name), ResourceRequest.TEXTURE, 1)).get());
    }
    
    /**
     * Returns the {@link com.opengg.core.texture.Texture} with the given name using the sRGB format<br>
     * It is acquired by calling {@link #getTexturePath(java.lang.String) } and passing the result into {@link com.opengg.core.render.texture.Texture#get2DSRGBTexture(java.lang.String) },
     * @param name Name of texture file to be loaded
     * @return Texture object loaded from file
     */
    public static Texture getSRGBTexture(String name){
        return Texture.get2DSRGBTexture((TextureData) ResourceManager.prefetch(new ResourceRequest(getTexturePath(name), ResourceRequest.TEXTURE, 1)).get());
    }
    
    /**
     * Returns the {@link com.opengg.core.texture.TextureData} with the given name <br>
     * It is acquired by calling {@link #getTexturePath(java.lang.String) } and passing the result into {@link com.opengg.core.render.texture.TextureManager#loadTextureData(java.lang.String) },
     * @param name Name of texture file to be loaded
     * @return Texture data object loaded from file
     */
    public static TextureData getTextureData(String name){
        return (TextureData) ResourceManager.prefetch(new ResourceRequest(getTexturePath(name), ResourceRequest.TEXTURE, 1)).get();
    }
    
    public static GGFont getFont(String fname, String ftexname){
        String fpath = getFontPath(fname);
        String tpath = getTexturePath(ftexname);
        return new GGFont(tpath,fpath);
    }
    
    /**
     * Sets the path used for all method calls to convert from relative to absolute paths
     * @param path Location of base directory for use
     */
    public static void setDefaultPath(String path){
        current = path;
    }
}