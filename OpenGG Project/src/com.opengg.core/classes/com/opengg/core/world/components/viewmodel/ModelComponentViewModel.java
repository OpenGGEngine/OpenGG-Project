/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.model.Model;
import com.opengg.core.model.ModelManager;
import com.opengg.core.world.components.ModelComponent;

/**
 *
 * @author Javier
 */
@ForComponent(com.opengg.core.world.components.ModelComponent.class)
public class ModelComponentViewModel extends ViewModel<ModelComponent>{
    
    @Override
    public Initializer getInitializer(Initializer init) {

        Element modelpath = new Element();
        modelpath.autoupdate = true;
        modelpath.type = Element.Type.MODEL;
        modelpath.name = "Model";
        modelpath.internalname = "model";
        modelpath.value = ModelManager.getDefaultModel();

        Element createCollider = new Element();
        createCollider.autoupdate = true;
        createCollider.type = Element.Type.BOOLEAN;
        createCollider.name = "Create Collider";
        createCollider.internalname = "createcollider";
        createCollider.value = false;

        init.elements.add(modelpath);
        init.elements.add(createCollider);
        return init;
    }

    @Override
    public ModelComponent getFromInitializer(Initializer init) {
        return new ModelComponent((Model)init.get("model").value, (boolean)init.get("createcollider").value);
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("model")){
            component.setModel((Model)element.value);
        }
    }

    @Override
    public void createMainViewModel() {
        Element modelpath = new Element();
        modelpath.autoupdate = true;
        modelpath.type = Element.Type.MODEL;
        modelpath.name = "Model";
        modelpath.internalname = "model";
        modelpath.value = ModelManager.getDefaultModel();
        
        elements.add(modelpath);
    }

    @Override
    public void updateView(Element element) {
       
    }
}
