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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author Javier
 */
public final class ResourceLoader {
    public static final int THREAD_AMOUNT = 4;
    private static ParallelWorkerPool<ResourceRequest, Resource> processor;
    
    private ResourceLoader(){}
    
    public static void initialize(){
        GGConsole.log("Initializing parallel resource loader with " + THREAD_AMOUNT + " worker thread(s)");
        
        processor = new ParallelWorkerPool<>(THREAD_AMOUNT, 
                request -> processRequest(request),
                (request, value) -> request.future.complete(value));
        
        processor.run();
    }

    private static Resource processRequest(ResourceRequest request){
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

    /**
     * Processes the given {@link ResourceRequest} synchronously and returns the result
     * @param request ResourceRequest that is to be processed
     * @return The {@link Resource} described by the resource request, or the default resources of its type if the one in the request fails to load
     */
    public static Resource get(ResourceRequest request){
        try {
            return prefetch(request).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Processes the given {@link ResourceRequest} asynchronously
     * @param request ResourceRequest that is to be processed
     * @return A {@link Future} of the {@link Resource} described by the request
     */
    public static Future<Resource> prefetch(ResourceRequest request){
        request.future = new CompletableFuture<>();
        
        if(request.type == ResourceRequest.TEXTURE){
            Resource r = TextureManager.getTextureData(request.location);
            if(r != TextureManager.getDefault()){
                request.future.complete(r);
                return request.future;
            }
        }

        if(request.type == ResourceRequest.SOUND){
            Resource r = SoundManager.getSoundData(request.location);
        }
        if(request.type == ResourceRequest.MODEL){
           Resource r = ModelManager.getModel(request.location);
           if(r != ModelManager.getDefaultModel()){
               request.future.complete(r);
               return request.future;
           }
        }

        processor.add(request, request.priority);

        return request.future;
    }
    
    public static boolean isRequested(String pos){
        for(ResourceRequest request : processor.getQueue()){
            if(request.location.equalsIgnoreCase(pos)) return true;
        }

        return false;
    }
}
