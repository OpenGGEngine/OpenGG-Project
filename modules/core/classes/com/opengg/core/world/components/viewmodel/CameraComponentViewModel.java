/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.world.components.CameraComponent;

/**
 *
 * @author Javier
 */
@ForComponent(CameraComponent.class)
public class CameraComponentViewModel extends ComponentViewModel<CameraComponent> {

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();
    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {
        return init;
    }

    @Override
    public CameraComponent getFromInitializer(BindingAggregate init) {
        return new CameraComponent();
    }
    
}
