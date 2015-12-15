/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.buffer;

import com.opengg.core.Vector3f;
import com.opengg.core.io.objloader.parser.OBJFace;
import com.opengg.core.io.objloader.parser.OBJMesh;
import com.opengg.core.io.objloader.parser.OBJModel;
import static com.opengg.core.util.GlobalUtil.print;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Javier
 */
public class ObjectBuffers {
    public static FloatBuffer genBuffer(OBJModel m, float transparency, float scale){
        
        List<OBJFace> f = m.getObjects().get(0).getMeshes().get(0).getFaces();
        print("The material file is "+ m.getMaterialLibraries());
        
        for(OBJMesh ms :m.getObjects().get(0).getMeshes()){
            print(ms.getMaterialName());
        }
       
        FloatBuffer elements = BufferUtils.createFloatBuffer(m.getVertices().size() * 78);
        for (OBJFace fa : f){

            int i1 = fa.getReferences().get(0).vertexIndex;
            int i2 = fa.getReferences().get(1).vertexIndex;
            int i3 = fa.getReferences().get(2).vertexIndex;
            
//            if(i1 > 11950){
//                continue;
//            }
            
            float x1 = m.getVertices().get(i1).x*scale;
            float y1 = m.getVertices().get(i1).y*scale;
            float z1 = m.getVertices().get(i1).z*scale;
            float x2 = m.getVertices().get(i2).x*scale;
            float y2 = m.getVertices().get(i2).y*scale;
            float z2 = m.getVertices().get(i2).z*scale;
            float x3 = m.getVertices().get(i3).x*scale;
            float y3 = m.getVertices().get(i3).y*scale;
            float z3 = m.getVertices().get(i3).z*scale;
            
            int ni1 = 0, ni2 = 0, ni3 = 0;
            float xn = 0.1f, yn = 0.1f, zn = 0.1f;
            float xn2 = 0.1f, yn2 = 0.1f, zn2 = 0.1f;
            float xn3 = 0.1f, yn3 = 0.1f, zn3 = 0.1f;
            float u = 0.1f, v = 0.1f;
            float u2 = 0.1f, v2 = 0.1f;
            float u3 = 0.1f, v3 = 0.1f;
            int uv1= 0, uv2 = 0, uv3 = 0;
            if(fa.hasNormals()){
                ni1 = fa.getReferences().get(0).normalIndex;
                ni2 = fa.getReferences().get(1).normalIndex;
                ni3 = fa.getReferences().get(2).normalIndex;
                xn = m.getNormals().get(ni1).x;
                yn = m.getNormals().get(ni1).y;
                zn = m.getNormals().get(ni1).z;
                xn2 = m.getNormals().get(ni2).x;
                yn2 = m.getNormals().get(ni2).y;
                zn2 = m.getNormals().get(ni2).z;
                xn3 = m.getNormals().get(ni3).x;
                yn3 = m.getNormals().get(ni3).y;
                zn3 = m.getNormals().get(ni3).z;
            }
            if(fa.hasTextureCoordinates()){
                uv1 = fa.getReferences().get(0).texCoordIndex;
                uv2 = fa.getReferences().get(1).texCoordIndex;
                uv3 = fa.getReferences().get(2).texCoordIndex;
                u = m.getTexCoords().get(uv1).u;
                v = m.getTexCoords().get(uv1).v;
                u2 = m.getTexCoords().get(uv2).u;
                v2 = m.getTexCoords().get(uv2).v;
                u3 = m.getTexCoords().get(uv3).u;
                v3 = m.getTexCoords().get(uv3).v;
            }
            Random random = new Random();
            float colorg = random.nextFloat() % 10;
            float colorr = random.nextFloat() % 10;
            float colorb = random.nextFloat() % 10; 
            elements.put(x1).put(y1).put(z1).put(colorr).put(colorg).put(colorb).put(transparency).put(xn).put(yn).put(zn).put(u).put(v);
            float colorg2 = random.nextFloat() % 10;
            float colorr2 = random.nextFloat() % 10;
            float colorb2 = random.nextFloat() % 10; 
            elements.put(x2).put(y2).put(z2).put(colorr2).put(colorg2).put(colorb2).put(transparency).put(xn2).put(yn2).put(zn2).put(u2).put(v2);
            float colorg3 = random.nextFloat() % 10;
            float colorr3 = random.nextFloat() % 10;
            float colorb3 = random.nextFloat() % 10; 
            elements.put(x3).put(y3).put(z3).put(colorr3).put(colorg3).put(colorb3).put(transparency).put(xn3).put(yn3).put(zn3).put(u3).put(v3);

        }
        
        elements.flip();
        return elements;
    }
    
    public static FloatBuffer genBuffer(OBJModel m, OBJMesh msh, float transparency, float scale){
        
        List<OBJFace> f = msh.getFaces();
       
        FloatBuffer elements = BufferUtils.createFloatBuffer(m.getVertices().size() * 78);
        for (OBJFace fa : f){

            int i1 = fa.getReferences().get(0).vertexIndex;
            int i2 = fa.getReferences().get(1).vertexIndex;
            int i3 = fa.getReferences().get(2).vertexIndex;
            
//            if(i1 > 11950){
//                continue;
//            }
            
            float x1 = m.getVertices().get(i1).x*scale;
            float y1 = m.getVertices().get(i1).y*scale;
            float z1 = m.getVertices().get(i1).z*scale;
            float x2 = m.getVertices().get(i2).x*scale;
            float y2 = m.getVertices().get(i2).y*scale;
            float z2 = m.getVertices().get(i2).z*scale;
            float x3 = m.getVertices().get(i3).x*scale;
            float y3 = m.getVertices().get(i3).y*scale;
            float z3 = m.getVertices().get(i3).z*scale;
            
            int ni1 = 0, ni2 = 0, ni3 = 0;
            float xn = 0.1f, yn = 0.1f, zn = 0.1f;
            float xn2 = 0.1f, yn2 = 0.1f, zn2 = 0.1f;
            float xn3 = 0.1f, yn3 = 0.1f, zn3 = 0.1f;
            float u = 0.1f, v = 0.1f;
            float u2 = 0.1f, v2 = 0.1f;
            float u3 = 0.1f, v3 = 0.1f;
            int uv1= 0, uv2 = 0, uv3 = 0;
            if(fa.hasNormals()){
                ni1 = fa.getReferences().get(0).normalIndex;
                ni2 = fa.getReferences().get(1).normalIndex;
                ni3 = fa.getReferences().get(2).normalIndex;
                xn = m.getNormals().get(ni1).x;
                yn = m.getNormals().get(ni1).y;
                zn = m.getNormals().get(ni1).z;
                xn2 = m.getNormals().get(ni2).x;
                yn2 = m.getNormals().get(ni2).y;
                zn2 = m.getNormals().get(ni2).z;
                xn3 = m.getNormals().get(ni3).x;
                yn3 = m.getNormals().get(ni3).y;
                zn3 = m.getNormals().get(ni3).z;
            }
            if(fa.hasTextureCoordinates()){
                uv1 = fa.getReferences().get(0).texCoordIndex;
                uv2 = fa.getReferences().get(1).texCoordIndex;
                uv3 = fa.getReferences().get(2).texCoordIndex;
                u = m.getTexCoords().get(uv1).u;
                v = m.getTexCoords().get(uv1).v;
                u2 = m.getTexCoords().get(uv2).u;
                v2 = m.getTexCoords().get(uv2).v;
                u3 = m.getTexCoords().get(uv3).u;
                v3 = m.getTexCoords().get(uv3).v;
            }
            Random random = new Random();
            float colorg = random.nextFloat() % 10;
            float colorr = random.nextFloat() % 10;
            float colorb = random.nextFloat() % 10; 
            elements.put(x1).put(y1).put(z1).put(colorr).put(colorg).put(colorb).put(transparency).put(xn).put(yn).put(zn).put(u).put(v);
            float colorg2 = random.nextFloat() % 10;
            float colorr2 = random.nextFloat() % 10;
            float colorb2 = random.nextFloat() % 10; 
            elements.put(x2).put(y2).put(z2).put(colorr2).put(colorg2).put(colorb2).put(transparency).put(xn2).put(yn2).put(zn2).put(u2).put(v2);
            float colorg3 = random.nextFloat() % 10;
            float colorr3 = random.nextFloat() % 10;
            float colorb3 = random.nextFloat() % 10; 
            elements.put(x3).put(y3).put(z3).put(colorr3).put(colorg3).put(colorb3).put(transparency).put(xn3).put(yn3).put(zn3).put(u3).put(v3);

        }
        
        elements.flip();
        return elements;
    }
    
    
      public static List<FloatBuffer> genMTLBuffer(OBJModel m, float transparency, float scale){
        List<FloatBuffer> buffets = new ArrayList();
        List<OBJFace> f = m.getObjects().get(0).getMeshes().get(0).getFaces();
        print("The material file is "+ m.getMaterialLibraries());
        
        for(OBJMesh ms :m.getObjects().get(0).getMeshes()){
            print(ms.getMaterialName());
            FloatBuffer elements = BufferUtils.createFloatBuffer(m.getVertices().size() * 78);
        for (OBJFace fa : ms.getFaces()){

            int i1 = fa.getReferences().get(0).vertexIndex;
            int i2 = fa.getReferences().get(1).vertexIndex;
            int i3 = fa.getReferences().get(2).vertexIndex;
            
//            if(i1 > 11950){
//                continue;
//            }
            
            float x1 = m.getVertices().get(i1).x*scale;
            float y1 = m.getVertices().get(i1).y*scale;
            float z1 = m.getVertices().get(i1).z*scale;
            float x2 = m.getVertices().get(i2).x*scale;
            float y2 = m.getVertices().get(i2).y*scale;
            float z2 = m.getVertices().get(i2).z*scale;
            float x3 = m.getVertices().get(i3).x*scale;
            float y3 = m.getVertices().get(i3).y*scale;
            float z3 = m.getVertices().get(i3).z*scale;
            
            int ni1 = 0, ni2 = 0, ni3 = 0;
            float xn = 0.1f, yn = 0.1f, zn = 0.1f;
            float xn2 = 0.1f, yn2 = 0.1f, zn2 = 0.1f;
            float xn3 = 0.1f, yn3 = 0.1f, zn3 = 0.1f;
            float u = 0.1f, v = 0.1f;
            float u2 = 0.1f, v2 = 0.1f;
            float u3 = 0.1f, v3 = 0.1f;
            int uv1= 0, uv2 = 0, uv3 = 0;
            if(fa.hasNormals()){
                ni1 = fa.getReferences().get(0).normalIndex;
                ni2 = fa.getReferences().get(1).normalIndex;
                ni3 = fa.getReferences().get(2).normalIndex;
                xn = m.getNormals().get(ni1).x;
                yn = m.getNormals().get(ni1).y;
                zn = m.getNormals().get(ni1).z;
                xn2 = m.getNormals().get(ni2).x;
                yn2 = m.getNormals().get(ni2).y;
                zn2 = m.getNormals().get(ni2).z;
                xn3 = m.getNormals().get(ni3).x;
                yn3 = m.getNormals().get(ni3).y;
                zn3 = m.getNormals().get(ni3).z;
            }
            if(fa.hasTextureCoordinates()){
                uv1 = fa.getReferences().get(0).texCoordIndex;
                uv2 = fa.getReferences().get(1).texCoordIndex;
                uv3 = fa.getReferences().get(2).texCoordIndex;
                u = m.getTexCoords().get(uv1).u;
                v = m.getTexCoords().get(uv1).v;
                u2 = m.getTexCoords().get(uv2).u;
                v2 = m.getTexCoords().get(uv2).v;
                u3 = m.getTexCoords().get(uv3).u;
                v3 = m.getTexCoords().get(uv3).v;
            }
            Random random = new Random();
            float colorg = random.nextFloat() % 10;
            float colorr = random.nextFloat() % 10;
            float colorb = random.nextFloat() % 10; 
            elements.put(x1).put(y1).put(z1).put(colorr).put(colorg).put(colorb).put(transparency).put(xn).put(yn).put(zn).put(u).put(v);
            float colorg2 = random.nextFloat() % 10;
            float colorr2 = random.nextFloat() % 10;
            float colorb2 = random.nextFloat() % 10; 
            elements.put(x2).put(y2).put(z2).put(colorr2).put(colorg2).put(colorb2).put(transparency).put(xn2).put(yn2).put(zn2).put(u2).put(v2);
            float colorg3 = random.nextFloat() % 10;
            float colorr3 = random.nextFloat() % 10;
            float colorb3 = random.nextFloat() % 10; 
            elements.put(x3).put(y3).put(z3).put(colorr3).put(colorg3).put(colorb3).put(transparency).put(xn3).put(yn3).put(zn3).put(u3).put(v3);

        }
        
        elements.flip();
        buffets.add(elements);
        }
        
       return buffets;
        
    }

    public static FloatBuffer getSquareUI(float x1, float x2, float y1, float y2, float z1 ,float transparency, boolean flippedTex){
        FloatBuffer sq = BufferUtils.createFloatBuffer(6*12);
        
        int i, i2;
        if(flippedTex){
            i = 1;
            i2 = 0;
            
        }else{
            i = 0;
            i2 = 1;
        }
        
        sq.put(x1).put(y1).put(z1).put(1).put(0).put(0).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(0).put(i);
        sq.put(x1).put(y2).put(z1).put(0).put(1).put(0).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(0).put(i2);
        sq.put(x2).put(y1).put(z1).put(0).put(0).put(1).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(1).put(i);
        sq.put(x2).put(y1).put(z1).put(0).put(1).put(0).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(1).put(i);
        sq.put(x2).put(y2).put(z1).put(0).put(0).put(1).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(1).put(i2);
        sq.put(x1).put(y2).put(z1).put(1).put(0).put(0).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(0).put(i2);
        
        sq.flip();
        return sq;
    }

    
    public static FloatBuffer getSquare(float x1, float z1, float x2, float y, float z2, float transparency){
        FloatBuffer sq = BufferUtils.createFloatBuffer(6*12);
        
        sq.put(x1).put(y).put(z1).put(1).put(0).put(0).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(1).put(0);
        sq.put(x1).put(y).put(z2).put(0).put(1).put(0).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(1).put(1);
        sq.put(x2).put(y).put(z1).put(0).put(0).put(1).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(0).put(0);
        sq.put(x2).put(y).put(z1).put(0).put(1).put(0).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(0).put(0);
        sq.put(x2).put(y).put(z2).put(0).put(0).put(1).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(0).put(1);
        sq.put(x1).put(y).put(z2).put(1).put(0).put(0).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(1).put(1);
        
        sq.flip();
        return sq;
    }
    public static FloatBuffer getSquare(float x1, float z1, float x2, float y1,float y2, float y3, float y4, float z2, float transparency,boolean flippedTex){
        FloatBuffer sq = BufferUtils.createFloatBuffer(6*12);
        int i, i2;
        if(flippedTex){
            i = 1;
            i2 = 0;
            
        }else{
            i = 0;
            i2 = 1;
        }
        
        sq.put(x1).put(y1).put(z1).put(1).put(0).put(0).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(1).put(i);
        sq.put(x1).put(y2).put(z2).put(0).put(1).put(0).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(1).put(i2);
        sq.put(x2).put(y3).put(z1).put(0).put(0).put(1).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(0).put(i);
        sq.put(x2).put(y3).put(z1).put(0).put(1).put(0).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(0).put(i);
        sq.put(x2).put(y4).put(z2).put(0).put(0).put(1).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(0).put(i2);
        sq.put(x1).put(y2).put(z2).put(1).put(0).put(0).put(transparency).put(0.1f).put(0.1f).put(0.1f).put(1).put(i2);
        
        sq.flip();
        return sq;
    }
    public static FloatBuffer getSquareTerrain(float x1, float z1, float x2, float z2, float y1,float y2, float y3, float y4, float transparency, float v1, float u1, float v2, float u2, Vector3f n1, Vector3f n2, Vector3f n3, Vector3f n4){
        FloatBuffer sq = BufferUtils.createFloatBuffer(6*12);
        
        sq.put(x1).put(y1).put(z1).put(1).put(1).put(1).put(transparency).put(n1.x).put(n1.y).put(n1.z).put(v1).put(u1);
        sq.put(x2).put(y2).put(z1).put(1).put(1).put(1).put(transparency).put(n2.x).put(n2.y).put(n2.z).put(v2).put(u1);
        sq.put(x2).put(y3).put(z2).put(1).put(1).put(1).put(transparency).put(n3.x).put(n3.y).put(n3.z).put(v2).put(u2);
        
        sq.put(x2).put(y3).put(z2).put(1).put(1).put(1).put(transparency).put(n3.x).put(n3.y).put(n3.z).put(v2).put(u2);
        sq.put(x1).put(y4).put(z2).put(1).put(1).put(1).put(transparency).put(n4.x).put(n4.y).put(n4.z).put(v1).put(u2);
        sq.put(x1).put(y1).put(z1).put(1).put(1).put(1).put(transparency).put(n1.x).put(n1.y).put(n1.z).put(v1).put(u1);
        
        sq.flip();
        
        return sq;
    }
    public static FloatBuffer createDefaultBufferData(int size){
        FloatBuffer f = BufferUtils.createFloatBuffer(size);
        for(int i = 0; i < size; i++){
            f.put(0);
        }
        f.flip();
        return f;
    }
    @SuppressWarnings("empty-statement")
    public static FloatBuffer genSkyCube(){
        FloatBuffer sq = BufferUtils.createFloatBuffer(6*6*12);
        
        sq.put(-1500f).put(1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);
        sq.put(-1500f).put(-1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(-1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(-1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(-1500f).put(1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        
        sq.put(-1500f).put(1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(-1500f).put(-1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(-1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(-1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(-1500f).put(1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        
        
        sq.put(-1500f).put(-1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(-1500f).put(-1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(-1500f).put(1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(-1500f).put(1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(-1500f).put(1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(-1500f).put(-1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        
        sq.put(1500f).put(-1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(-1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(-1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        
        
        sq.put(-1500f).put(1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(-1500f).put(1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(-1500f).put(1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        
        sq.put(-1500f).put(-1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(-1500f).put(-1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(-1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(-1500f).put(-1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(1500f).put(-1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        sq.put(-1500f).put(-1500f).put(1500f).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);;
        
        sq.flip();
        return sq;
    }
}
