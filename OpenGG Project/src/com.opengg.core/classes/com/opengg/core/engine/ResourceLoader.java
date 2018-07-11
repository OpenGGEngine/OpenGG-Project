/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.engine;

import com.opengg.core.audio.SoundManager;
import com.opengg.core.console.GGConsole;
import com.opengg.core.model.Material;
import com.opengg.core.model.Mesh;
import com.opengg.core.model.Model;
import com.opengg.core.model.ModelManager;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.thread.ParallelWorkerPool;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Javier
 */
public final class ResourceLoader {
    public static final int THREAD_AMOUNT = 4;
    private static final Map<ResourceRequest, ResourceFuture> maps = new HashMap<>();
    private static ParallelWorkerPool<ResourceRequest, Resource> processor;
    
    private ResourceLoader(){}
    
    public static void initialize(){
        GGConsole.log("Initializing parallel resource loader with " + THREAD_AMOUNT + " worker thread(s)");
        
        processor = new ParallelWorkerPool<>(THREAD_AMOUNT, 
                request -> maps.remove(maps.get(request).set(processRequest(request)).request).get());
        
        processor.run();
    }
    
    private static Resource processRequest(ResourceRequest request){
        ///maps.get(request).processing = true;
        
        if(request.type == ResourceRequest.TEXTURE){
            return TextureManager.loadTexture(request.location);
        }

        if(request.type == ResourceRequest.SOUND){
             return SoundManager.loadSound(request.location);
        }

        if(request.type == ResourceRequest.MODEL){
            Model model = ModelManager.loadModel(request.location);
            for(Mesh mesh : model.getMeshes()){
                Material material = mesh.getMaterial();
                if (material.mapKdFilename != null && !material.mapKdFilename.isEmpty()) {
                    TextureManager.loadTexture(material.texpath + material.mapKdFilename);
                }
                if (material.mapKaFilename != null && !material.mapKaFilename.isEmpty()) {
                    TextureManager.loadTexture(material.texpath + material.mapKaFilename);
                }
                if (material.mapKsFilename != null && !material.mapKsFilename.isEmpty()) {
                    TextureManager.loadTexture(material.texpath + material.mapKsFilename);
                }
                if (material.mapNsFilename != null && !material.mapNsFilename.isEmpty()) {
                    TextureManager.loadTexture(material.texpath + material.mapNsFilename);
                }
                if (material.mapDFilename != null && !material.mapDFilename.isEmpty()) {
                    TextureManager.loadTexture(material.texpath + material.mapDFilename);
                }
                if (material.bumpFilename != null && !material.bumpFilename.isEmpty()) {
                    TextureManager.loadTexture(material.texpath + material.bumpFilename);
                }
            }
            return model;
        }
        return null;
    }
    
    public static Resource get(ResourceRequest request){
        return prefetch(request).get();
    }
    
    public static ResourceFuture prefetch(ResourceRequest request){
        ResourceFuture future = new ResourceFuture();
        future.request = request;
        
        if(request.type == ResourceRequest.TEXTURE){
            Resource r = TextureManager.getTextureData(request.location);
            if(r != TextureManager.getDefault()){
                future.set(r);
                return future;
            }
        }

        if(request.type == ResourceRequest.SOUND){
            Resource r = SoundManager.getSoundData(request.location);
        }
        if(request.type == ResourceRequest.MODEL){
           Resource r = ModelManager.getModel(request.location);
           if(r != ModelManager.getDefaultModel()){
               future.set(r);
               return future;
           }
        }
        
        maps.put(request, future);
        processor.add(request, request.priority);

        return future;
    }
    
    public static boolean isRequested(String pos){
        for(ResourceFuture request : maps.values()){
            if(request.request.location.equalsIgnoreCase(pos)) return true;
        }
        return false;
    }
    
    public static boolean isProcessing(String pos){
        for(ResourceFuture future : maps.values()){
            if(future.request.location.equalsIgnoreCase(pos) && future.isProcessing()) return true;
        }
        return false;
    }
}
