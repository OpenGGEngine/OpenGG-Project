/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

/**
 *
 * @author Warren
 */
public class Face {
    
    public FaceVertex v1,v2,v3;
    public int adj1 = -1, adj2 = -1, adj3 = -1;

    public Face() {}
    
    public Face(FaceVertex v1, FaceVertex v2, FaceVertex v3){
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    @Override
    public String toString() {
        return v1 +  ", " + v2 + ", " + v3;
    }
}  
