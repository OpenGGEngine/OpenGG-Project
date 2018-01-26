/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import static com.opengg.core.model.ModelUtil.*;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Warren
 */
public class Mesh {
    public List<Face> faces = new ArrayList<>();
    public FloatBuffer vbodata;
    public IntBuffer inddata;
    public Material material;
    public boolean animated = false;
    
    public Mesh(List<Face> faces){
        this(faces, Material.defaultmaterial);
    }
    
    public Mesh(List<Face> faces, Material m) {
        this.faces = faces;
        this.material = m;
        makeadamnvbo(this, false);
    }
    
    public Mesh(FloatBuffer fb, IntBuffer ib, Material m, boolean animated){
        updateVBO(fb, ib);
        this.material = m;
        makeadamnfacelist(this, animated);
    }
    
    public Mesh(FloatBuffer fb, IntBuffer ib, List<Face> faces, Material m){
        this.faces = faces;
        this.vbodata = fb;
        this.inddata = ib;
        this.material = m;
    }
    
    public void updateVBO(FloatBuffer fb, IntBuffer ib){
        vbodata = fb;
        inddata = ib;
    }
    
    public void putData(GGOutputStream out) throws IOException{
        vbodata.rewind();
        out.write(vbodata);
        vbodata.rewind();
        
        inddata.rewind();
        out.write(inddata);
        inddata.rewind();
        
        material.toFileFormat(out);
    }

    public List<Face> getFaces() {
        return faces;
    }

    public void setFaces(List<Face> faces) {
        this.faces = faces;
    }

    public FloatBuffer getVBOdata() {
        return vbodata;
    }

    public void setVBOdata(FloatBuffer vbodata) {
        this.vbodata = vbodata;
    }

    public IntBuffer getIndexData() {
        return inddata;
    }

    public void getIndexData(IntBuffer inddata) {
        this.inddata = inddata;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }
    
    
}
