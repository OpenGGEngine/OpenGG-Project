/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.model.Model;
import com.opengg.core.model.ModelManager;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ModelRenderComponent;

/**
 *
 * @author Javier
 */
public class ModelRenderComponentViewModel extends ViewModel{
    
    @Override
    public Initializer getInitializer(Initializer init) {

        Element modelpath = new Element();
        modelpath.autoupdate = true;
        modelpath.type = Element.MODEL;
        modelpath.name = "Model";
        modelpath.internalname = "model";
        modelpath.value = ModelManager.getDefaultModel();
        
        init.elements.add(modelpath);
        return init;
    }

    @Override
    public Component getFromInitializer(Initializer init) {
        ModelRenderComponent ncomponent = new ModelRenderComponent((Model)init.elements.get(0).value);
        return ncomponent;
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("model")){
            ((ModelRenderComponent)component).setModel((Model)element.value);
        }
    }

    @Override
    public void createMainViewModel() {
        Element modelpath = new Element();
        modelpath.autoupdate = true;
        modelpath.type = Element.MODEL;
        modelpath.name = "Model";
        modelpath.internalname = "model";
        modelpath.value = ModelManager.getDefaultModel();
        
        elements.add(modelpath);
    }

    @Override
    public void updateViews() {
       
    }
}