/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.Initializer;
import com.opengg.core.world.components.CameraComponent;

/**
 *
 * @author Javier
 */
@ForComponent(CameraComponent.class)
public class CameraComponentViewModel extends ViewModel<CameraComponent>{

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public CameraComponent getFromInitializer(Initializer init) {
        return new CameraComponent();
    }
    
}
