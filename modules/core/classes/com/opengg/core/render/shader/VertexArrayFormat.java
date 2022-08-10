/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Javier
 */
public class VertexArrayFormat{
    private int hashcode = 0;
    final List<VertexArrayBinding> bindingSites;

    public VertexArrayFormat(List<VertexArrayBinding> bindingSites) {
        this.bindingSites = bindingSites;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VertexArrayFormat that = (VertexArrayFormat) o;
        return bindingSites.equals(that.bindingSites);
    }

    @Override
    public int hashCode() {
        if(hashcode == 0){
            hashcode = Objects.hash(bindingSites);
        }
        return hashcode;
    }
}
