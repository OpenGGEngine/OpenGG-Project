/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model.modelloaderplus;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.DrawnObjectGroup;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author warre
 */
public class MModel {
    public ArrayList<MMesh> meshes = new ArrayList<>();
    public List<MNode> mnodes = new ArrayList<>();
    public MNode root;
    public String name;
    public double duration,tickspeed;
    public MModel(String name){
        this.name = name;
    }
    public void addMesh(MMesh mesh){
        meshes.add(mesh);
    }

    public DrawnObjectGroup toRenderable(){
        DrawnObjectGroup s = new DrawnObjectGroup();
        for(MMesh mesh:meshes){
            DrawnObject temp  = new DrawnObject(RenderEngine.animation2VAOFormat,mesh.ibo,mesh.vbo);
            s.add(temp);
        }
        return s;
    }
}
