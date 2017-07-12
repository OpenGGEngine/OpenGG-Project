/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.WorldObject;

/**
 *
 * @author Javier
 */
public class WorldObjectViewModel extends ComponentViewModel{

    @Override
    public void createMainViewModel() {
        
    }

    @Override
    public ViewModelInitializer getInitializer() {
        return new ViewModelInitializer();
    }

    @Override
    public Component getFromInitializer(ViewModelInitializer init) {
        return new WorldObject();
    }

    @Override
    public void onChange(ViewModelElement element) {
        
    }

    @Override
    public void updateViews() {
       
    }
    
}
