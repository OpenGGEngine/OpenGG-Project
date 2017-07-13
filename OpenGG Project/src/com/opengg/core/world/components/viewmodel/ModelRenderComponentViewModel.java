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
public class ModelRenderComponentViewModel extends ViewModel{
    
    @Override
    public Initializer getInitializer() {
        Initializer initializer = new Initializer();
        
        Element modelpath = new Element();
        modelpath.autoupdate = false;
        modelpath.type = Element.STRING;
        modelpath.name = "Model Path";
        modelpath.internalname = "path";
        modelpath.value = "";
        
        initializer.elements.add(modelpath);
        return initializer;
    }

    @Override
    public Component getFromInitializer(Initializer init) {
        String path = (String) init.elements.get(0).value;
        ModelRenderComponent ncomponent = new ModelRenderComponent(ModelLoader.loadModel(path));
        return ncomponent;
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("path")){
            ((ModelRenderComponent)component).setModel(ModelLoader.loadModel((String)element.value));
        }
    }

    @Override
    public void createMainViewModel() {
        Element modelpath = new Element();
        modelpath.autoupdate = false;
        modelpath.type = Element.STRING;
        modelpath.name = "Model Path";
        modelpath.internalname = "path";
        modelpath.value = "";
        
        elements.add(modelpath);
    }

    @Override
    public void updateViews() {
       
    }
}
