/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.DrawnObjectGroup;
import com.opengg.core.render.drawn.MatDrawnObject;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Warren
 */
public class Model {
    private String name;
    private List<Mesh> meshes = new ArrayList<>();
    private Drawable drawable = null;
    public Model(String name, List<Mesh> meshes){
        this.name = name;
        this.meshes = meshes;
    }
    
    public List<Mesh> getMeshes(){
        return meshes;
    }
    
    public Model(Build b) {
        name = b.getObjectName();
        HashMap<String, ArrayList<BuilderFace>> facesByTextureList = new HashMap<>();

        for (BuilderFace face : b.faces) {

            if (face.material == null) {
                face.material = new Material("default");
            }

            if (facesByTextureList.containsKey(face.material.toString())) {
                ArrayList<BuilderFace> temp = facesByTextureList.get(face.material.toString());
                temp.add(face);
                facesByTextureList.replace(face.material.toString(), temp);
            } else {
                ArrayList<BuilderFace> temp = new ArrayList<>();
                temp.add(face);
                facesByTextureList.put(face.material.toString(), temp);
            }
        }
        for (String key : facesByTextureList.keySet()) {
            ArrayList<BuilderFace> currentFaceList = facesByTextureList.get(key);
            currentFaceList = ModelUtil.splitQuads(currentFaceList);
            
            Material material = currentFaceList.get(0).material;
            List<Face> faces = ModelUtil.builderToFace(currentFaceList);         
            
            Mesh obj = new Mesh(faces, material);
            meshes.add(obj);
        }
    }

    public Drawable getDrawable(){
        if(drawable != null)
            return drawable;
        
        GGConsole.log("Drawable for " + name + " has been requested, loading textures...");
        List<Drawable> draws = new ArrayList<>();
        for(Mesh mesh : meshes){
            ModelUtil.findAdjacencies(mesh);
            DrawnObject dr = new DrawnObject(mesh.vbodata, mesh.inddata);
            dr.setAdjacency(true);
            draws.add(new MatDrawnObject(dr, mesh.m));
        }
        drawable = new DrawnObjectGroup(draws);
        return drawable;
    }

    public void putData(String path) throws FileNotFoundException, IOException {
        FileOutputStream ps;

        ps = new FileOutputStream(path + name + ".bmf");
        try (DataOutputStream dos = new DataOutputStream(ps)) {
            dos.writeInt(meshes.size());
            for (Mesh m : meshes) {
                FloatBuffer fs = m.vbodata;
                IntBuffer ib = m.inddata;
                
                dos.writeInt(fs.capacity());
                for (float f : fs.array()) {
                    dos.writeFloat(f);
                }
                dos.writeInt(ib.capacity());
                for (int i : ib.array()) {
                    dos.writeInt(i);
                }
                m.m.toFileFormat(dos);
            }
        }
        ps.close();
       
        
        System.out.println("Finished putting data for " + name);
    }
    
    public String getName(){
        return name;
    }
}
