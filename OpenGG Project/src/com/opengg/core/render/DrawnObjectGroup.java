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
    //The list of materials are in order so the nth value in list objs
    //corresponds to the nth value in list materials
    public DrawnObjectGroup(URL u, int scale){
        String path = u.getPath();
        try {
            OBJModel m = new OBJParser().parse(u);
            System.out.println(m.getMaterialLibraries());
            final InputStream in = new FileInputStream("C:/res/"+m.getMaterialLibraries().get(0));
            final IMTLParser parser = new MTLParser();
            final MTLLibrary library = parser.parse(in);
            List<OBJMesh> msh = m.getObjects().get(0).getMeshes();
            
            for(OBJMesh ms : msh){
                materials.add(library.getMaterial(ms.getMaterialName()));
                objs.add(new DrawnObject(ObjectBuffers.genBuffer(m, ms, 1, 0.08f), 12));
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
        
        for(DrawnObject d : objs){
            d.drawShaded();
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
