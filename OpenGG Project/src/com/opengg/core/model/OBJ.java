/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.io.newobjloader.Face;
import com.opengg.core.io.newobjloader.FaceVertex;
import com.opengg.core.io.newobjloader.Material;
import com.opengg.core.io.newobjloader.Model;
import com.opengg.core.io.newobjloader.Parser;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObjectGroup;
import com.opengg.core.render.drawn.MatDrawnObject;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.util.GlobalInfo;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Javier
 */
public class OBJ {
    public static DrawnObjectGroup getDrawableModel(String s){
        Parser p = new Parser();
        ArrayList<Drawable> objs = new ArrayList<>();
        Model model = new Model();
        try {
            model = p.parseModel(s);
        } catch (IOException ex) {
            Logger.getLogger(DrawnObjectGroup.class.getName()).log(Level.SEVERE, null, ex);
        }
        String temp2 = model.objFilename.substring(model.objFilename.lastIndexOf("/"), model.objFilename.length()-4);
        String name = temp2;               
        HashMap<String,ArrayList<Face>> facesByTextureList= new HashMap<>();

        
        for (Face face : model.faces) {
            
            if(face.material == null){
                face.material = Material.defaultmaterial;
                
            }
            
            if(facesByTextureList.containsKey(face.material.toString())){
                
                ArrayList<Face> temp = facesByTextureList.get(face.material.toString());
                temp.add(face);
                facesByTextureList.replace(face.material.toString(), temp);
            }else{
                ArrayList<Face> temp = new ArrayList<>();
                temp.add(face);
                facesByTextureList.put(face.material.toString(), temp);
            }
            
        }
       for (String key : facesByTextureList.keySet()) {
            ArrayList<Face> currentFaceList = facesByTextureList.get(key);
            currentFaceList = splitQuads(currentFaceList);
            MatDrawnObject obj = makeadamnvbo(currentFaceList);
            Material material = currentFaceList.get(0).material;
            
            obj.setM(material);
            
            if (material.mapKdFilename == null) {
            } else {
                Texture nointernet = new Texture();
                nointernet.loadTexture("C:/res/"+name+"/" + material.mapKdFilename, true);
                obj.setTexture(nointernet);
            }
            if(material.mapNsFilename !=null){              
                Texture nointernet = new Texture();
                nointernet.loadTexture("C:/res/"+name+"/" + material.mapNsFilename, true);
                obj.setSpecularMap(nointernet);
            }
            if(material.bumpFilename !=null){
                Texture nointernet = new Texture();
                nointernet.loadTexture("C:/res/"+name+"/" + material.bumpFilename, true);
                obj.setNormalMap(nointernet);
            }
            objs.add(obj);
        }
        return new DrawnObjectGroup(objs);
        
    }
    public static MatDrawnObject makeadamnvbo(ArrayList<Face> triangles){
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
        FloatBuffer verticeAttributes = BufferUtils.createFloatBuffer(faceVertexList.size() * 78);
       
      
        for (FaceVertex vertex : faceVertexList) {
            verticeAttributes.put(vertex.v.x);
            verticeAttributes.put(vertex.v.y);
            verticeAttributes.put(vertex.v.z);
            verticeAttributes.put(1);
            verticeAttributes.put(0.4f);
            verticeAttributes.put(24);
            verticeAttributes.put(1);
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

       

        IntBuffer indices = BufferUtils.createIntBuffer(indicesCount);    // indices into the vertices, to specify triangles.
       
        for (Face face : triangles) {
            for (FaceVertex vertex : face.vertices) {
                int index = indexMap.get(vertex);
                indices.put(index);
            }
        }
        indices.flip();
        return new MatDrawnObject(verticeAttributes,GlobalInfo.b,indices);
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
}
