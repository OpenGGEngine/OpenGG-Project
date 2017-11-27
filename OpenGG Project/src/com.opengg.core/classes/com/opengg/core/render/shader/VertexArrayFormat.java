/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import java.util.ArrayList;
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
        return attribs;
    }
    
    public int getVertexLength(){
        int l = 0;
        
        for(VertexArrayAttribute attrib : attribs){
            l += attrib.size;
        }
        
        return l;
        
    }
    
    @Override
    public boolean equals(Object o){
        if(!(o instanceof VertexArrayFormat))
            return false;
        VertexArrayFormat form = (VertexArrayFormat)o;
        
        boolean match = false;
        init : for(VertexArrayAttribute vaa : attribs){
            for(VertexArrayAttribute vaa2 : form.attribs){
                if(vaa.equals(vaa2))
                    continue init;
            }
            return false;
        }
        
        init : for(VertexArrayAttribute vaa : form.attribs){
            for(VertexArrayAttribute vaa2 : attribs){
                if(vaa.equals(vaa2))
                    continue init;
            }
            return false;
        }   
        return true;
    }
}
