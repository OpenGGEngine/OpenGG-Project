/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io.newobjloader;

import com.opengg.core.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Warren
 */

public class Face {

    public ArrayList<FaceVertex> vertices = new ArrayList<>();
    public Material material;
    public Material map;

    public Face() {
    }

    public void add(FaceVertex vertex) {
        vertices.add(vertex);
    }
    public Vector3f faceNormal = new Vector3f(0, 0, 0);

    // @TODO: This code assumes the face is a triangle.  
    public void calculateTriangleNormal() {
        float[] edge1 = new float[3];
        float[] edge2 = new float[3];
        float[] normal = new float[3];
        Vector3f v1 = vertices.get(0).v;
        Vector3f v2 = vertices.get(1).v;
        Vector3f v3 = vertices.get(2).v;
        float[] p1 = {v1.x, v1.y, v1.z};
        float[] p2 = {v2.x, v2.y, v2.z};
        float[] p3 = {v3.x, v3.y, v3.z};

        edge1[0] = p2[0] - p1[0];
        edge1[1] = p2[1] - p1[1];
        edge1[2] = p2[2] - p1[2];

        edge2[0] = p3[0] - p2[0];
        edge2[1] = p3[1] - p2[1];
        edge2[2] = p3[2] - p2[2];

        normal[0] = edge1[1] * edge2[2] - edge1[2] * edge2[1];
        normal[1] = edge1[2] * edge2[0] - edge1[0] * edge2[2];
        normal[2] = edge1[0] * edge2[1] - edge1[1] * edge2[0];

        faceNormal.x = normal[0];
        faceNormal.y = normal[1];
        faceNormal.z = normal[2];
    }
    
    @Override
    public String toString() { 
        String result = "\tvertices: "+vertices.size()+" :\n";
        for(FaceVertex f : vertices) {
            result += " \t\t( "+f.toString()+" )\n";
        }
        return result;
    }
}   
