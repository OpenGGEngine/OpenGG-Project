/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.engine;

import com.opengg.core.GGInfo;
import com.opengg.core.audio.SoundData;
import com.opengg.core.console.GGConsole;
import com.opengg.core.model.Model;
import com.opengg.core.model.ModelManager;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.TTF;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.text.impl.GGFont;
import java.io.File;
import java.io.IOException;

/**
 * Used for loading and managing resources used in the engine <br>
 *     Additionally, all subclasses of this interface are implementations of these types of resources
 * @author Javier
 */
public interface Resource {
    enum Type{
        MODEL, TEXTURE, SOUND, WORLD
    }

    /**
     * Returns the type of resource that this Resource represents
     * @return
     */
    Resource.Type getType();

    /**
     * Returns the source of this resource
     * @return
     */
    String getSource();
    
    static void initialize(){
        GGConsole.log("Initializing resource system in " + GGInfo.getApplicationPath());
        ResourceLoader.initialize();
    }
    
    /**
     * Returns the absolute version of the path given a relative path, using the GGInfo.getApplicationPath() directory of the OpenGG runtime as the starting point of the relative path. <br><br>
     * If the given path is already absolute, it will return the path unchanged
     * 
     * @param name Relative path to be converted
     * @return Absolute version of the path
     */
    static String getAbsoluteFromLocal(String name){
        try {
            if(new File(name).isAbsolute()) return name;
            return new File(GGInfo.getApplicationPath(), name).getCanonicalPath();
        } catch (IOException ex) {
            GGConsole.warning("Failed to load " + name + "!");
        }
        return null;
    }

    static String getApplicationPath(){
        return GGInfo.getApplicationPath();
    }

    static String getUserDataPath(){
        if(GGInfo.getUserDataLocation() == GGInfo.UserDataOption.DOCUMENTS)
            return System.getProperty("user.home") + File.separator + "Documents" + File.separator + GGInfo.getApplicationName() + File.separator;
        else
            return System.getenv("APPDATA") + File.separator + GGInfo.getApplicationName() + File.separator;
    }

    /**
     * Returns if this filename is absolute
     * @param name Filename
     * @return
     */
    static boolean isAbsolute(String name){
        return new File(name).isAbsolute();
    }

    /**
     * Returns if the file given by this path exists
     * @param name Filename
     * @return
     */
    static boolean exists(String name){
        return new File(name).exists();
    }


    private static boolean validate(String name){
        if(isAbsolute(name)) return true;
        if(exists(Resource.getApplicationPath() + File.separator + name)) return true;
        return exists(Resource.getUserDataPath() + File.separator + name);
    }
    
    /**
     * Returns the path to a model with the given name, with the format resources/models/$name$/$name$.bmf
     * @param name Name of model, not including the .bmf extension
     * @return Relative path to the model
     */
    static String getModelPath(String name){
        if(validate(name)) return name;
        if(name.contains(".bmf"))
            return "resources" + File.separator + "models" + File.separator + name;
        else
            return "resources" + File.separator + "models" + File.separator + name + File.separator + name + ".bmf";
    }
    
    /**
     * Returns the path to a configuration file with the given name, with the format cfg/$name$.cfg
     * @param name Name of configuration file, not including the .cfg extension
     * @return Relative path to the file
     */
    static String getConfigPath(String name){
        if(validate(name)) return name;
        return "cfg" + File.separator + name + ".cfg";
    }
    
    /**
     * Returns the path to a shader file with the given name, with the format resources/glsl/$name$
     * @param name Name of shader file, including the respective file ending
     * @return Relative path to the shader file
     */
    static String getShaderPath(String name){
        if(validate(name)) return name;
        return "resources" + File.separator + "glsl" + File.separator +  name;
    }
    
    /**
     * Returns the path to a texture with the given name, with the format resources/tex/$name$
     * @param name Name of texture, including the respective file ending
     * @return Relative path to the texture
     */
    static String getTexturePath(String name){
        if(validate(name)) return name;
        return "resources" + File.separator + "tex" + File.separator +  name;
    }
    
    /**
     * Returns the path to a font file with the given name, with the format resources/font/$name$.fnt
     * @param name Name of font file, not including the .fnt extension
     * @return Relative path to the font file
     */
    static String getFontPath(String name){
        if(validate(name)) return name;
        return "resources" + File.separator + "font" + File.separator +  name;
    }
    
    /**
     * Returns the path to a sound file with the given name, with the format resources/audio/$name$
     * @param name Name of sound, including the respective file ending
     * @return Relative path to the sound
     */
    static String getSoundPath(String name){
        if(validate(name)) return name;
        return "resources" + File.separator + "audio" + File.separator +  name;
    }
    
    /**
     * Returns the path to a world with the given name, with the format resources/worlds/$name$.bwf
     * @param name Name of world, not including the .bwf extension
     * @return Relative path to the world file
     */
    static String getWorldPath(String name){
        if(validate(name)) return name;
        return "resources" + File.separator + "worlds" + File.separator +  name + ".bwf";
    }
    
    /**
     * Returns the {@link com.opengg.core.audio.SoundData} with the given name <br>
     * It is acquired by calling {@link #getSoundPath(java.lang.String) } and passing the result into {@link com.opengg.core.audio.SoundManager#loadSound(java.lang.String) }
     * @param name Name of sound file to be loaded
     * @return SoundData object loaded from file
     */
    static SoundData getSoundData(String name){
        return (SoundData) ResourceLoader.get(new ResourceRequest(getSoundPath(name), Resource.Type.SOUND, 1));
    }
    
    /**
     * Returns the {@link com.opengg.core.model.Model} with the given name <br>
     * It is acquired by calling {@link #getModelPath(java.lang.String) } and passing the result into {@link ModelManager#loadModel(java.lang.String) }
     * @param name Name of model file to be loaded
     * @return Model object loaded from file
     */
    static Model getModel(String name){
        return (Model) ResourceLoader.get(new ResourceRequest(getModelPath(name), Resource.Type.MODEL, 1));
    }
    
    /**
     * Returns the {@link com.opengg.core.render.texture.Texture} with the given name <br>
     * It is acquired by calling {@link #getTexturePath(java.lang.String) } and passing the result into {@link com.opengg.core.render.texture.Texture#get2DTexture(java.lang.String) },
     * @param name Name of texture file to be loaded
     * @return Texture object loaded from file
     */
    static Texture getTexture(String name){
        return Texture.get2DTexture((TextureData) ResourceLoader.get(new ResourceRequest(name, Resource.Type.TEXTURE, 1)));
    }
    
    /**
     * Returns the {@link com.opengg.core.render.texture.Texture} with the given name using the sRGB format<br>
     * It is acquired by calling {@link #getTexturePath(java.lang.String) } and passing the result into {@link com.opengg.core.render.texture.Texture#get2DSRGBTexture(java.lang.String) },
     * @param name Name of texture file to be loaded
     * @return Texture object loaded from file
     */
    static Texture getSRGBTexture(String name){
        return Texture.get2DSRGBTexture((TextureData) ResourceLoader.get(new ResourceRequest(name, Resource.Type.TEXTURE, 1)));
    }
    
    /**
     * Returns the {@link com.opengg.core.render.texture.TextureData} with the given name <br>
     * @param name Name of texture file to be loaded
     * @return Texture data object loaded from file
     */
    static TextureData getTextureData(String name){
        return (TextureData) ResourceLoader.get(new ResourceRequest(name, Resource.Type.TEXTURE, 1));
    }
    
    static GGFont getFont(String fname, String ftexname){
        String fpath = getFontPath(fname);
        String tpath = getTexturePath(ftexname);
        return new GGFont(tpath,fpath);
    }

    /**
     * Gets a font stored in the TrueType format with the given name
     * @param path Name/path of font
     * @return
     */
    static Font getTruetypeFont(String path){
        return TTF.getTruetypeFont(getFontPath(path), true);
    }
    
    /**
     * Sets the path used for all method calls to convert from relative to absolute paths
     * @param path Location of base directory for use
     */
    static void setDefaultPath(String path){
        GGInfo.setApplicationPath(path);
    }
}
