/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import static com.opengg.core.model.ModelUtil.*;
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
    public Material m;
    public boolean adjacency = false;
    
    public boolean hasAdjacencyData(){
        return adjacency;
    }
    
    public Mesh(List<Face> faces){
        this(faces, Material.defaultmaterial);
    }
    
    public Mesh(List<Face> faces, Material m) {
        this.faces = faces;
        this.m = m;
        findAdjacencies(this);
        makeadamnadjacencyvbo(this);
    }
    
    public Mesh(FloatBuffer fb, IntBuffer ib, Material m){
        updateVBO(fb, ib);
        this.m = m;
        makeadamnfacelist(this);
        findAdjacencies(this);
        makeadamnadjacencyvbo(this);
    }
    public Mesh(FloatBuffer fb, IntBuffer ib, Material m,boolean overide){
        updateVBO(fb, ib);
        this.m = m;
        if(overide){
        makeadamnfacelist(this);
        findAdjacencies(this);
        makeadamnadjacencyvbo(this);
        }
    }
    
    public Mesh(FloatBuffer fb, IntBuffer ib, Material m, int[] adjacencies){
        updateVBO(fb, ib);
        this.m = m;
        makeadamnfacelist(this, adjacencies);
        makeadamnadjacencyvbo(this);
    }
    
    public Mesh(FloatBuffer fb, IntBuffer ib, List<Face> faces, Material m){
        this.faces = faces;
        this.vbodata = fb;
        this.inddata = ib;
        this.m = m;
    }
    
    public void updateVBO(FloatBuffer fb, IntBuffer ib){
        vbodata = fb;
        inddata = ib;
    }
}
