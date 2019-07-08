/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

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
        Element color = new Element();
        color.type = Element.Type.VECTOR3F;
        color.internalname = "color";
        color.name = "Light Color";
        color.value = new Vector3f(1,1,1);

        Element distance = new Element();
        distance.type = Element.Type.FLOAT;
        distance.internalname = "distance";
        distance.name = "Light Distance";
        distance.value = 100f;
        
        elements.add(color);
        elements.add(distance);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public LightComponent getFromInitializer(Initializer init) {
        return new LightComponent();
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("color")) {
            component.getLight().setColor((Vector3f)element.value);
        } else if(element.internalname.equals("distance")) {
            component.getLight().setDistance((Float)element.value);
        }
        component.update(0);
    }

    @Override
    public void updateView(Element element) {
        getByName("color").value = component.getLight().getColor();
        getByName("distance").value = component.getLight().getDistance();
    } 
}
