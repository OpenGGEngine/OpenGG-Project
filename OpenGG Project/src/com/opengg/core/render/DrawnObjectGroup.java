/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render;

import com.opengg.core.Matrix4f;
import com.opengg.core.io.objloader.parser.OBJMesh;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.io.objloader.parser.OBJParser;
import com.opengg.core.render.buffer.ObjectBuffers;
import java.io.IOException;
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
    
    public DrawnObjectGroup(URL u, int scale){
        String path = u.getPath();
        try {
            OBJModel m = new OBJParser().parse(u);
            
            List<OBJMesh> msh = m.getObjects().get(0).getMeshes();
            
            for(OBJMesh ms : msh){
                objs.add(new DrawnObject(ObjectBuffers.genBuffer(m, ms, 1, scale), 12));
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
