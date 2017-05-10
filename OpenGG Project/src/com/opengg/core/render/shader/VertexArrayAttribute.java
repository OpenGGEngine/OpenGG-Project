/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

/**
 *
 * @author Javier
 */
public class VertexArrayAttribute {
    String name;
    int size;
    int offset;
    int arrayindex;
    boolean divisor;
    
    public VertexArrayAttribute(String name, int size, int offset, int index, boolean divisor){
        this.name = name;
        this.size = size;
        this.offset = offset;
        this.arrayindex = index;
        this.divisor = divisor;
    }
}
