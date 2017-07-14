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
    public String name;
    public int size;
    public int offset;
    public int arrayindex;
    public boolean divisor;
    
    public VertexArrayAttribute(String name, int size, int offset, int index, boolean divisor){
        this.name = name;
        this.size = size;
        this.offset = offset;
        this.arrayindex = index;
        this.divisor = divisor;
    }
    
    @Override
    public boolean equals(Object o){
        if(!(o instanceof VertexArrayAttribute))
            return false;
        VertexArrayAttribute other = (VertexArrayAttribute)o;
        if(this.size != other.size)
            return false;
        if(this.arrayindex != other.arrayindex)
            return false;
        if(this.divisor != other.divisor)
            return false;
        if(this.offset != other.offset)
            return false;
        if(!(this.name.equals(other.name)))
            return false;
        return true;
    }
}
