/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class GLBufferHandler {
    static List<GLBuffer> buffers = new ArrayList<>(); 
    
    public static void destroy(){
        for(GLBuffer buffer : buffers){
            buffer.delete();
        }
    }
    
    public long getTotalSize(){
        long l = 0;
        for(GLBuffer buffer : buffers){
            l += buffer.getSize();
        }
        return l;
    }
}
