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
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Warren
 */
public class Model {
    public static int mversion = 1;
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
        GGConsole.log(name + " has been loaded, generating meshes...");
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
        facesByTextureList.keySet()
            .parallelStream()
            .map((key) -> facesByTextureList.get(key))
            .map((currentFaceList) -> ModelUtil.splitQuads(currentFaceList))
            .map((currentFaceList) -> {
                Material material = currentFaceList.get(0).material;
                List<Face> faces = ModelUtil.builderToFace(currentFaceList);
                Mesh obj = new Mesh(faces, material);
                return obj;})
            .forEach((obj) -> {
                meshes.add(obj);
        });
    }

    public void generateDrawable(){
        GGConsole.log("Drawable for " + name + " has been requested, loading textures...");
        List<Drawable> draws = new ArrayList<>();
        for(Mesh mesh : meshes){
            DrawnObject dr = new DrawnObject(mesh.vbodata, mesh.inddata);
            dr.setAdjacency(true);
            draws.add(new MatDrawnObject(dr, mesh.m));
        }
        drawable = new DrawnObjectGroup(draws);
    }
    
    public Drawable getDrawable(){
        if(drawable != null)
            return drawable;
        generateDrawable();
        return drawable;
    }

    public void putData(String path) throws FileNotFoundException, IOException {
        FileOutputStream ps;
        ModelUtil.makeadamnvbo(meshes);
        GGConsole.log("Writing model data to file...");
        ps = new FileOutputStream(path + name + ".bmf");
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(ps))) {
            //dos.writeInt(mversion);
            dos.writeInt(meshes.size());
            for (Mesh m : meshes) {
                dos.writeInt(m.vbodata.capacity());
                for (float f : m.vbodata.array()) {
                    dos.writeFloat(f);
                }
                
                dos.writeInt(m.inddata.capacity());
                for (int i : m.inddata.array()) {
                    dos.writeInt(i);
                }
                
                dos.writeInt(m.faces.size());
                for(Face f : m.faces){
                    dos.writeInt(f.adj1);
                    dos.writeInt(f.adj2);
                    dos.writeInt(f.adj3);
                }
                m.m.toFileFormat(dos);
            }
        }
        ps.close();
       
        
        GGConsole.log("Finished putting data for " + name);
    }
    
    public String getName(){
        return name;
    }
}
