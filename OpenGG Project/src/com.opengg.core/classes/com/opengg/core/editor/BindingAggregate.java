/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.editor;

import com.opengg.core.editor.DataBinding;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class BindingAggregate {
    private List<DataBinding> dataBindings = new ArrayList<>();
    
    public void addElement(DataBinding e){
        dataBindings.add(e);
    }

    public List<DataBinding> getDataBindings(){
        return dataBindings;
    }
    
    public DataBinding get(String name){
        for(DataBinding dataBinding : dataBindings){
            if(dataBinding.internalname.equalsIgnoreCase(name)) return dataBinding;
        }
        return null;
    }
}
