/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.buffer;

import com.opengg.core.objloader.parser.OBJFace;
import com.opengg.core.objloader.parser.OBJModel;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Javier
 */
public class ObjectBuffers {
    public static FloatBuffer genBuffer(OBJModel m, float transparency){
        
        List<OBJFace> f = m.getObjects().get(0).getMeshes().get(0).getFaces();
        
        FloatBuffer elements = BufferUtils.createFloatBuffer(m.getVertices().size() * 100);
        for (OBJFace fa : f){

            int i1 = fa.getReferences().get(0).vertexIndex;
            int i2 = fa.getReferences().get(1).vertexIndex;
            int i3 = fa.getReferences().get(2).vertexIndex;

            float x1 = m.getVertices().get(i1).x;
            float y1 = m.getVertices().get(i1).y;
            float z1 = m.getVertices().get(i1).z;
            float x2 = m.getVertices().get(i2).x;
            float y2 = m.getVertices().get(i2).y;
            float z2 = m.getVertices().get(i2).z;
            float x3 = m.getVertices().get(i3).x;
            float y3 = m.getVertices().get(i3).y;
            float z3 = m.getVertices().get(i3).z;
            
            int ni1 = 0, ni2 = 0, ni3 = 0;
            float xn = 0, yn = 0, zn = 0;
            float xn2 = 0, yn2 = 0, zn2 = 0;
            float xn3 = 0, yn3 = 0, zn3 = 0;
            float u = 0, v = 0;
            float u2 = 0, v2 = 0;
            float u3 = 0, v3 = 0;
            int ui = 0, vi = 0;
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
                ui = fa.getReferences().get(0).normalIndex;
                vi = fa.getReferences().get(1).normalIndex;
                u = m.getTexCoords().get(ui).u;
                v = m.getTexCoords().get(ui).v;
                u2 = m.getTexCoords().get(ui).u;
                v2 = m.getTexCoords().get(ui).v;
                u3 = m.getTexCoords().get(ui).u;
                v3 = m.getTexCoords().get(ui).v;
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
}
