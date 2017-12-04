/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.console.GGConsole;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 *
 * @author Warren
 */
public class Model {
    public Map<String, Animation> animations = new HashMap<>();
    private List<Mesh> meshes = new ArrayList<>();
    
    public boolean isanimated;
    public static int mversion = 1;
    private String name;
    
    private ModelDrawnObject drawable = null;
    
    public Model(String name, List<Mesh> meshes){
        this.name = name;
        this.meshes = meshes;
        this.isanimated = false;
    }
    
    public Model(String name, List<Mesh> meshes, Map<String, Animation> animations){
        this.name = name;
        this.meshes = meshes;
        this.animations = animations;
        this.isanimated = true;
    }
    
    public List<Mesh> getMeshes(){
        return meshes;
    }

    private ModelDrawnObject generateDrawable(){
        GGConsole.log("Drawable for " + name + " has been requested, loading textures...");
        return new ModelDrawnObject(this);
    }
    
    public Drawable getDrawable(){
        if(drawable != null)
            return drawable;
        drawable = generateDrawable();
        return drawable;
    }

    public void putData(GGOutputStream out) throws IOException {
        GGConsole.log("Writing model data...");
        out.write(1);
        out.write(isanimated);
        
        out.write(meshes.size());
        for (Mesh mesh : meshes) {
            mesh.putData(out);
        }
        
        out.write("animcheck");
        
        if(isanimated){
            out.write(animations.size());
            for(String s : animations.keySet()){
                animations.get(s).writeBuffer(out);
            }
        }
        
        GGConsole.log("Finished putting data for " + name);
    }
    
    public String getName(){
        return name;
    }
}
