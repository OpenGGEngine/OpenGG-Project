/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.drawn;

import com.opengg.core.Matrix4f;
import com.opengg.core.io.objloader.parser.IMTLParser;
import com.opengg.core.io.objloader.parser.MTLLibrary;
import com.opengg.core.io.objloader.parser.MTLMaterial;
import com.opengg.core.io.objloader.parser.MTLParser;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.io.objloader.parser.OBJParser;
import com.opengg.core.render.buffer.ObjectBuffers;
import com.opengg.core.render.texture.Texture;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier
 */
public class DrawnObjectGroup implements Drawable{
    List<MatDrawnObject> objs = new ArrayList<>();
  
    //The list of materials are in order so the nth value in list objs
    //corresponds to the nth value in list materials
    public DrawnObjectGroup(URL u, float scale){  
        try {
            
            
            
           
            OBJModel m = new OBJParser().parse(u);
            
            final MTLLibrary library;
            
            try (InputStream in = new FileInputStream("C:/res/" + u.getFile().substring(u.getFile().lastIndexOf("/")).replace(".obj", "") + "/"+m.getMaterialLibraries().get(0))) {
                final IMTLParser parser = new MTLParser();
               
                library = parser.parse(in);
            }
            
            m.getObjects().stream().forEach((o) -> {
                o.getMeshes().stream().map((ms) -> {
                    MTLMaterial material = library.getMaterial(ms.getMaterialName());
                    MatDrawnObject d = new MatDrawnObject(ObjectBuffers.genBuffer(m,ms,1,scale),12);
                    d.setMaterial(material);
                    if(material.getDiffuseTexture() != null){
                        Texture nointernet = new Texture();
                        nointernet.loadTexture("C:/res/"+ material.getDiffuseTexture(), true);
                        d.setTexture(nointernet);
                    }
                    if(material.getSpecularTexture() != null){
                        Texture nointernet = new Texture();
                        nointernet.loadTexture("C:/res/"+ material.getSpecularTexture(), true);
                        d.setSpecularMap(nointernet);
                    }
                    return d;
                }).forEach((d) -> {
                    objs.add(d);
                });
            });
        } catch (IOException ex) {
            Logger.getLogger(DrawnObjectGroup.class.getName()).log(Level.SEVERE, null, ex);
        }
   
        
        
    }
    
    
    @Override
    public void drawShaded(){
         
        objs.stream().forEach((d) -> {
            d.drawShaded();
        });      
    }
    
    @Override
    public void saveShadowMVP(){
        objs.stream().forEach((d) -> {
            d.saveShadowMVP();
        }); 
    }
    
    public void setShaderMatrix(Matrix4f m){
        objs.stream().forEach((d) -> {
            d.setShaderMatrix(m);
        }); 
    }
    
    @Override
    public void draw(){
        objs.stream().forEach((d) -> {
            d.draw();
        });   
    }
    
    @Override
    public void drawPoints() {
        objs.stream().forEach((d) -> {
            d.drawPoints();
        }); 
    }
    
    @Override
    public void setMatrix(Matrix4f model){
        objs.stream().forEach((d) -> {
            d.setMatrix(model);
        }); 
    }
    
    @Override
    public Matrix4f getMatrix() {
        return new Matrix4f();
    }

    @Override
    public void destroy() {
        objs.stream().forEach((d) -> {
            d.destroy();
        }); 
    }

}