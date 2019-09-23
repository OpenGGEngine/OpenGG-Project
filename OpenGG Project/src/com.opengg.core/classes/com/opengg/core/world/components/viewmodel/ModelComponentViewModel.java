/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.Initializer;
import com.opengg.core.model.Model;
import com.opengg.core.model.ModelManager;
import com.opengg.core.world.components.ModelComponent;

/**
 *
 * @author Javier
 */
@ForComponent(com.opengg.core.world.components.ModelComponent.class)
public class ModelComponentViewModel extends RenderComponentViewModel<ModelComponent>{
    
    @Override
    public Initializer getInitializer(Initializer init) {

        DataBinding<Model> modelpath = DataBinding.ofType(DataBinding.Type.MODEL);
        modelpath.autoupdate = true;
        modelpath.name = "Model";
        modelpath.internalname = "model";
        modelpath.setValueFromData(ModelManager.getDefaultModel());

        DataBinding<Boolean> createCollider = DataBinding.ofType(DataBinding.Type.BOOLEAN);
        createCollider.autoupdate = true;
        createCollider.name = "Create Collider";
        createCollider.internalname = "createcollider";
        createCollider.setValueFromData(false);

        init.addElement(modelpath);
        init.addElement(createCollider);
        return init;
    }

    @Override
    public ModelComponent getFromInitializer(Initializer init) {
        return new ModelComponent((Model)init.get("model").getValue(), (boolean)init.get("createcollider").getValue());
    }

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        DataBinding<Model> modelpath = DataBinding.ofType(DataBinding.Type.MODEL);
        modelpath.autoupdate = true;
        modelpath.name = "Model";
        modelpath.internalname = "model";
        modelpath.setValueAccessorFromData(() -> component.getModel());
        modelpath.onViewChange(component::setModel);

        
        addElement(modelpath);
    }
}
