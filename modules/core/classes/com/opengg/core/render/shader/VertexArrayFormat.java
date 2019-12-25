/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Javier
 */
public class VertexArrayFormat {
    List<VertexArrayBinding> bindingSites = new ArrayList<>();
    
    public VertexArrayFormat(){}
    
    public VertexArrayFormat addBinding(VertexArrayBinding attrib){
        bindingSites.add(attrib);
        sort();
        return this;
    }
    
    private void sort(){
        bindingSites.sort(Comparator.comparingInt(VertexArrayBinding::getBindingIndex));
    }
    
    public List<VertexArrayBinding> getBindings(){
        return List.copyOf(bindingSites);
    }
    
    public int getVertexLength(){
        return bindingSites.stream()
                .filter(attrib -> attrib.getBindingIndex() == 0)
                .mapToInt(VertexArrayBinding::getVertexSize)
                .sum();
    }

    @Override
    public String toString(){
        return bindingSites.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VertexArrayFormat that = (VertexArrayFormat) o;

        return Objects.equals(bindingSites, that.bindingSites);

    }

    @Override
    public int hashCode() {
        return bindingSites != null ? bindingSites.hashCode() : 0;
    }
}
