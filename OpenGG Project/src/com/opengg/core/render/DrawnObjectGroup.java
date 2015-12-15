/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render;

import com.opengg.core.Matrix4f;
import com.opengg.core.io.objloader.parser.IMTLParser;
import com.opengg.core.io.objloader.parser.MTLLibrary;
import com.opengg.core.io.objloader.parser.MTLMaterial;
import com.opengg.core.io.objloader.parser.MTLParser;
import com.opengg.core.io.objloader.parser.OBJMesh;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.io.objloader.parser.OBJParser;
import com.opengg.core.render.buffer.ObjectBuffers;
import com.opengg.core.render.shader.ShaderHandler;
import com.opengg.core.render.texture.Texture;
import static com.opengg.core.util.GlobalUtil.print;
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
public class DrawnObjectGroup {
    List<DrawnObject> objs = new ArrayList<>();
    List<MTLMaterial> materials = new ArrayList<>();
    List<Texture> textures = new ArrayList<>();
     
    //The list of materials are in order so the nth value in list objs
    //corresponds to the nth value in list materials
    public DrawnObjectGroup(URL u, float scale){
        String path = u.getPath();
        
       
        try {
            OBJModel m = new OBJParser().parse(u);
            
            final InputStream in = new FileInputStream("C:/res/"+m.getMaterialLibraries().get(0));
            final IMTLParser parser = new MTLParser();
            print(m.getMaterialLibraries().get(0));
            final MTLLibrary library = parser.parse(in);
            in.close();
            List<OBJMesh> msh = m.getObjects().get(0).getMeshes();
           print(msh.size());
            for(OBJMesh ms : msh){
                Texture nointernet = new Texture();
            
                
               
                materials.add(library.getMaterial(ms.getMaterialName()));
                
                
                nointernet.loadTexture("C:/res/"+library.getMaterial(ms.getMaterialName()).getDiffuseTexture(), true);
                objs.add(new DrawnObject(ObjectBuffers.genBuffer(m, ms, 1, scale), 12));
                textures.add(nointernet);
             
                
            }
        } catch (IOException ex) {
            Logger.getLogger(DrawnObjectGroup.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    public void draw(){
        
        for(DrawnObject d : objs){
            
            
            
        
           
            d.draw();
        
                
           
          
        }   
    }
    
    public void drawShaded(){
         int counter=0;
        for(DrawnObject d : objs){
            
            textures.get(counter).useTexture(0);
            d.drawShaded();
             counter++;
             
        }      
    }
    
    public void saveShadowMVP(){
        for(DrawnObject d : objs){
            d.saveShadowMVP();
        } 
    }
    
    public void setShaderMatrix(Matrix4f m){
        for(DrawnObject d : objs){
            d.setShaderMatrix(m);
        } 
    }
    
    public void setModel(Matrix4f model){
        for(DrawnObject d : objs){
            d.setModel(model);
        } 
    }

}
