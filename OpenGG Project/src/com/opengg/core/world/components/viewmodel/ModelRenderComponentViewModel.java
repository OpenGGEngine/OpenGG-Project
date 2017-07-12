/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.model.ModelLoader;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ModelRenderComponent;

/**
 *
 * @author Javier
 */
public class ModelRenderComponentViewModel extends ComponentViewModel{
    
    @Override
    public ViewModelInitializer getInitializer() {
        ViewModelInitializer initializer = new ViewModelInitializer();
        
        ViewModelElement modelpath = new ViewModelElement();
        modelpath.autoupdate = false;
        modelpath.type = ViewModelElement.STRING;
        modelpath.name = "Model Path";
        modelpath.internalname = "path";
        modelpath.value = "";
        modelpath.cmv = this;
        
        initializer.elements.add(modelpath);
        return initializer;
    }

    @Override
    public Component getFromInitializer(ViewModelInitializer init) {
        String path = (String) init.elements.get(0).value;
        ModelRenderComponent ncomponent = new ModelRenderComponent(ModelLoader.loadModel(path));
        return ncomponent;
    }

    @Override
    public void onChange(ViewModelElement element) {
        if(element.internalname.equals("path")){
            ((ModelRenderComponent)component).setModel(ModelLoader.loadModel((String)element.value));
        }
    }

    @Override
    public void createMainViewModel() {
        ViewModelElement modelpath = new ViewModelElement();
        modelpath.autoupdate = false;
        modelpath.type = ViewModelElement.STRING;
        modelpath.name = "Model Path";
        modelpath.internalname = "path";
        modelpath.value = "";
        modelpath.cmv = this;
        
        elements.add(modelpath);
    }
}
