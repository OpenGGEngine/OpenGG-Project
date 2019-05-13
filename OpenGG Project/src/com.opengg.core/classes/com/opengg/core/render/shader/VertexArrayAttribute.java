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
public class VertexArrayAttribute{
    public String name;
    public int offset;
    public int type;
    public int size;

    public VertexArrayAttribute(String name, int size, int type, int offset){
        this.name = name;
        this.offset = offset;
        this.size = size;
        this.type = type;
    }
    
    @Override
    public boolean equals(Object o){
        if(!(o instanceof VertexArrayAttribute))
            return false;
        VertexArrayAttribute other = (VertexArrayAttribute)o;
        if(this.offset != other.offset)
            return false;
        return this.name.equals(other.name);
    }
}
