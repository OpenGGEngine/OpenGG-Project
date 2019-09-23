/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.Initializer;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.LightComponent;

/**
 *
 * @author Javier
 */
@ForComponent(LightComponent.class)
public class LightComponentViewModel extends ViewModel<LightComponent>{

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var color = new DataBinding.Vector3fBinding();
        color.internalname = "color";
        color.name = "Light Color";
        color.setValueAccessorFromData(() -> component.getLight().getColor());
        color.onViewChange(c -> component.getLight().setColor(c));

        var distance = new DataBinding.FloatBinding();
        distance.internalname = "distance";
        distance.name = "Light Distance";
        distance.setValueAccessorFromData(() -> component.getLight().getDistance());
        distance.onViewChange(d -> component.getLight().setDistance(d));
        
        this.addElement(color);
        this.addElement(distance);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public LightComponent getFromInitializer(Initializer init) {
        return new LightComponent();
    }
}
