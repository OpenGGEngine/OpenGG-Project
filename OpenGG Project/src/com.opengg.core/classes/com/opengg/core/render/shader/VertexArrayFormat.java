/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Javier
 */
public class VertexArrayFormat {
    List<VertexArrayAttribute> attribs = new ArrayList<>();
    
    public VertexArrayFormat(){}
    
    public VertexArrayFormat addAttribute(VertexArrayAttribute attrib){
        attribs.add(attrib);
        sort();
        return this;
    }
    
    private void sort(){
        attribs.sort((c,c2)->{
            if(c.arrayindex < c2.arrayindex)
                return -1;
            if(c.arrayindex == c2.arrayindex)
                return 0;
            return 1;
        });
    }
    
    public List<VertexArrayAttribute> getAttributes(){
        return List.copyOf(attribs);
    }
    
    public int getVertexLength(){
        return attribs.stream()
                .filter(attrib -> attrib.arrayindex == 0)
                .mapToInt(attrib -> attrib.size)
                .sum();
    }

    @Override
    public String toString(){
        return attribs.toString();
    }

    @Override
    public boolean equals(Object o){
        if(o == this)
            return true;

        if(!(o instanceof VertexArrayFormat))
            return false;

        VertexArrayFormat form = (VertexArrayFormat)o;

        if(form.attribs.size() != this.attribs.size())
            return false;

        return true;
    }
}
