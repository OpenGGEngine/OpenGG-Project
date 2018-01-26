/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.engine;

import com.opengg.core.audio.SoundManager;
import com.opengg.core.model.Material;
import com.opengg.core.model.Mesh;
import com.opengg.core.model.Model;
import com.opengg.core.model.ModelManager;
import com.opengg.core.render.texture.TextureManager;

/**
 *
 * @author Javier
 */
public class ResourceProcessorThread implements Runnable{

    @Override
    public void run() {
        while(!OpenGG.getEnded()){
            try {
                Thread.sleep(1, (int) (Math.random()*100000));
            } catch (InterruptedException ex) {
                return;
            }
            
            ResourceRequest request = ResourceManager.getRequest();
            if(request != null){
                ResourceManager.addToProcessing(request);
                if(request.type == ResourceRequest.TEXTURE){
                    TextureManager.loadTexture(request.location);
                }
                
                if(request.type == ResourceRequest.SOUND){
                     SoundManager.loadSound(request.location);
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
                }
                ResourceManager.removeFromProcessing(request);
                request.completed = true;
            }
        }
    }
    
}
