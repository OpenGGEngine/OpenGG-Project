/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io.newobjloader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Warren
 */
public class Implementation {
    public static void makeadamnvbo(ArrayList<Face> triangles){
        HashMap<FaceVertex, Integer> indexMap = new HashMap<>();
        int nextVertexIndex = 0;
        ArrayList<FaceVertex> faceVertexList = new ArrayList<>();
        for (Face face : triangles) {
            for (FaceVertex vertex : face.vertices) {
                if (!indexMap.containsKey(vertex)) {
                    indexMap.put(vertex, nextVertexIndex++);
                    faceVertexList.add(vertex);
                }
            }
        }
        int verticeAttributesCount = nextVertexIndex;
        int indicesCount = triangles.size() * 3;

        int numMIssingNormals = 0;
        int numMissingUV = 0;
        FloatBuffer verticeAttributes = null;
       
      
        for (FaceVertex vertex : faceVertexList) {
            verticeAttributes.put(vertex.v.x);
            verticeAttributes.put(vertex.v.y);
            verticeAttributes.put(vertex.v.z);
            if (vertex.n == null) {
                // @TODO: What's a reasonable default normal?  Maybe add code later to calculate normals if not present in .obj file.
                verticeAttributes.put(1.0f);
                verticeAttributes.put(1.0f);
                verticeAttributes.put(1.0f);
                numMIssingNormals++;
            } else {
                verticeAttributes.put(vertex.n.x);
                verticeAttributes.put(vertex.n.y);
                verticeAttributes.put(vertex.n.z);
            }
            // @TODO: What's a reasonable default texture coord?  
            if (vertex.t == null) {
//                verticeAttributes.put(0.5f);
//                verticeAttributes.put(0.5f);
                    verticeAttributes.put((float)Math.random());
                    verticeAttributes.put((float)Math.random());
                numMissingUV++;
            } else {
                verticeAttributes.put(vertex.t.x);
                verticeAttributes.put(vertex.t.y);
            }
        }
        verticeAttributes.flip();

       

        IntBuffer indices = null;    // indices into the vertices, to specify triangles.
       
        for (Face face : triangles) {
            for (FaceVertex vertex : face.vertices) {
                int index = indexMap.get(vertex);
                indices.put(index);
            }
        }
        indices.flip();
    }
    public static ArrayList<Face> splitQuads(ArrayList<Face> faceList) {
        ArrayList<Face> triangleList = new ArrayList<>();
        int countTriangles = 0;
        int countQuads = 0;
        int countNGons = 0;
        for (Face face : faceList) {
            if (face.vertices.size() == 3) {
                countTriangles++;
                triangleList.add(face);
            } else if (face.vertices.size() == 4) {
                countQuads++;
                FaceVertex v1 = face.vertices.get(0);
                FaceVertex v2 = face.vertices.get(1);
                FaceVertex v3 = face.vertices.get(2);
                FaceVertex v4 = face.vertices.get(3);
                Face f1 = new Face();
                f1.map = face.map;
                f1.material = face.material;
                f1.add(v1);
                f1.add(v2);
                f1.add(v3);
                triangleList.add(f1);
                Face f2 = new Face();
                f2.map = face.map;
                f2.material = face.material;
                f2.add(v1);
                f2.add(v3);
                f2.add(v4);
                triangleList.add(f2);
            } else {
                countNGons++;
            }
        }
        int texturedCount = 0;
        int normalCount = 0;
        for (Face face : triangleList) {
            if ((face.vertices.get(0).n != null)
                && (face.vertices.get(1).n != null)
                && (face.vertices.get(2).n != null)) {
                normalCount++;
            }
            if ((face.vertices.get(0).t != null)
                && (face.vertices.get(1).t != null)
                && (face.vertices.get(2).t != null)) {
                texturedCount++;
            }
        }
       
        return triangleList;
    }
   public static ArrayList<ArrayList<Face>> createFaceListsByMaterial(Model builder) {
        ArrayList<ArrayList<Face>> facesByTextureList = new ArrayList<>();
        Material currentMaterial = null;
        ArrayList<Face> currentFaceList = new ArrayList<>();
        for (Face face : builder.faces) {
            if (face.material != currentMaterial) {
                if (!currentFaceList.isEmpty()) {
                    
                    facesByTextureList.add(currentFaceList);
                }
               
                currentMaterial = face.material;
                currentFaceList = new ArrayList<>();
            }
            currentFaceList.add(face);
        }
        if (!currentFaceList.isEmpty()) {
            
            facesByTextureList.add(currentFaceList);
        }
        return facesByTextureList;
    }
}
