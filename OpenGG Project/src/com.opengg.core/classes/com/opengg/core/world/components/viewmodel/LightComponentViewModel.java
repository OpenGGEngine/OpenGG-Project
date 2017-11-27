/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.LightComponent;

/**
 *
 * @author Javier
 */
public class LightComponentViewModel extends ViewModel{

    @Override
    public void createMainViewModel() {
        Element color = new Element();
        color.type = Element.VECTOR3F;
        color.internalname = "color";
        color.name = "Light Color";
        color.value = new Vector3f(1,1,1);

        Element distance = new Element();
        distance.type = Element.FLOAT;
        distance.internalname = "distance";
        distance.name = "Light Distance";
        distance.value = 100f;
        
        elements.add(color);
        elements.add(distance);
    }

    @Override
    public Initializer getInitializer() {
        return new Initializer();
    }

    @Override
    public Component getFromInitializer(Initializer init) {
        return new LightComponent();
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("color"))
            ((LightComponent)component).getLight().setColor((Vector3f)element.value);
        
        if(element.internalname.equals("distance"))
            ((LightComponent)component).getLight().setDistance((Float)element.value);
        ((LightComponent)component).update(0);
    }

    @Override
    public void updateViews() {
        getByName("color").value = ((LightComponent)component).getLight().getColor();
        getByName("distance").value = ((LightComponent)component).getLight().getDistance();
    } 
}
