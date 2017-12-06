/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Initializer {
    public List<Element> elements = new ArrayList<>();
    
    public void addElement(Element e){
        elements.add(e);
    }
    
    public Element get(String name){
        for(Element element : elements){
            if(element.internalname.equalsIgnoreCase(name)) return element;
        }
        return null;
    }
}
