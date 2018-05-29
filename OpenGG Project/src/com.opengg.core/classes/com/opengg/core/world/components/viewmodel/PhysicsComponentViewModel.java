/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.physics.PhysicsComponent;

/**
 *
 * @author Javier
 */
@ForComponent(PhysicsComponent.class)
public class PhysicsComponentViewModel extends ViewModel{

    @Override
    public void createMainViewModel() {
        Element mass = new Element();
        mass.type = Element.Type.FLOAT;
        mass.autoupdate = true;
        mass.name = "Mass";
        mass.internalname = "mass";
        mass.value = 100f;
        
        Element density = new Element();
        density.type = Element.Type.FLOAT;
        density.autoupdate = true;
        density.name = "Density";
        density.internalname = "density";
        density.value = 1f;
        
        Element restitution = new Element();
        restitution.type = Element.Type.FLOAT;
        restitution.autoupdate = true;
        restitution.name = "Restitution";
        restitution.internalname = "restitution";
        restitution.value = 0.5f;
        
        Element sfriction = new Element();
        sfriction.type = Element.Type.FLOAT;
        sfriction.autoupdate = true;
        sfriction.name = "Static friction";
        sfriction.internalname = "sfriction";
        sfriction.value = 0.6f;
        
        Element dfriction = new Element();
        dfriction.type = Element.Type.FLOAT;
        dfriction.autoupdate = true;
        dfriction.name = "Static friction";
        dfriction.internalname = "sfriction";
        dfriction.value = 0.6f;
        
        elements.add(mass);
        elements.add(density);
        elements.add(restitution);
        elements.add(sfriction);
        elements.add(dfriction);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public Component getFromInitializer(Initializer init) {
        return new PhysicsComponent();
    }

    @Override
    public void onChange(Element element) {
        switch (element.internalname) {
            case "name":
                ((PhysicsComponent)component).getEntity().name = (String)element.value;
                break;
            case "mass":
                ((PhysicsComponent)component).getEntity().mass = (Float)element.value;
                break;
            case "density":
                ((PhysicsComponent)component).getEntity().density = (Float)element.value;
                break;
            case "restitution":
                ((PhysicsComponent)component).getEntity().restitution = (Float)element.value;
                break;
            case "sfriction":
                ((PhysicsComponent)component).getEntity().staticfriction = (Float)element.value;
                break;
            case "dfriction":
                ((PhysicsComponent)component).getEntity().dynamicfriction = (Float)element.value;
                break;
            default:
                break;
        }
    }

    @Override
    public void updateViews() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
