/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glGenBuffers;

/**
 *
 * @author Javier
 */
public class DrawnObjectHandler {
    static long currentOffset = 0;
    static long currentIndexOffset = 0;
    static int elementBuffer;
    
    public static void setup(){
        
        elementBuffer = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer);

    }
    
    public static long getOffset(){
        return currentOffset;
    }
    public static void addToOffset(long offset){

        currentOffset += offset;
    }
    public static long getIndexOffset(){
        return currentIndexOffset;
    }
    public static void addToIndexOffset(long offset){

        currentIndexOffset += offset;
    }
}
