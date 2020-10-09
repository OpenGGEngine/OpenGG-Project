/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Javier
 */
public record VertexArrayFormat(List<VertexArrayBinding> bindingSites) {
    public VertexArrayFormat addBinding(VertexArrayBinding attrib){
        bindingSites.add(attrib);
        sort();
        return this;
    }
    
    private void sort(){
        bindingSites.sort(Comparator.comparingInt(VertexArrayBinding::bindingIndex));
    }
    
    public List<VertexArrayBinding> getBindings(){
        return List.copyOf(bindingSites);
    }
    
    public int getPrimaryVertexLength(){
        return bindingSites.stream()
                .filter(attrib -> attrib.bindingIndex() == 0)
                .mapToInt(VertexArrayBinding::vertexSize)
                .sum();
    }

}
