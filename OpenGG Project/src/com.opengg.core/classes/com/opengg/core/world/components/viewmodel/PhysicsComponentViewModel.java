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
public class PhysicsComponentViewModel extends ViewModel{

    @Override
    public void createMainViewModel() {
        Element mass = new Element();
        mass.type = Element.FLOAT;
        mass.autoupdate = true;
        mass.name = "Mass";
        mass.internalname = "mass";
        mass.value = 100f;
        
        Element density = new Element();
        density.type = Element.FLOAT;
        density.autoupdate = true;
        density.name = "Density";
        density.internalname = "density";
        density.value = 1f;
        
        Element fcoeff = new Element();
        fcoeff.type = Element.FLOAT;
        fcoeff.autoupdate = true;
        fcoeff.name = "Friction Coefficient";
        fcoeff.internalname = "fcoeff";
        fcoeff.value = 0.5f;
        
        Element bounciness = new Element();
        bounciness.type = Element.FLOAT;
        bounciness.autoupdate = true;
        bounciness.name = "Bounciness";
        bounciness.internalname = "bounciness";
        bounciness.value = 0.5f;
        
        Element friction = new Element();
        friction.type = Element.BOOLEAN;
        friction.autoupdate = true;
        friction.name = "Has Friction";
        friction.internalname = "friction";
        friction.value = true;
        
        elements.add(mass);
        elements.add(density);
        elements.add(fcoeff);
        elements.add(bounciness);
        elements.add(friction);
    }

    @Override
    public Initializer getInitializer() {
        return new Initializer();
    }

    @Override
    public Component getFromInitializer(Initializer init) {
        return new PhysicsComponent();
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("mass"))
            ((PhysicsComponent)component).getEntity().mass = (Float)element.value;
        
        if(element.internalname.equals("density"))
            ((PhysicsComponent)component).getEntity().density = (Float)element.value;
        
        if(element.internalname.equals("fcoeff"))
            ((PhysicsComponent)component).getEntity().dynamicfriction = (Float)element.value;
        
        if(element.internalname.equals("bounciness"))
            ((PhysicsComponent)component).getEntity().restitution = (Float)element.value;
        
        if(element.internalname.equals("friction"))
            ((PhysicsComponent)component).getEntity().dynamicfriction = (Float)element.value;
    }

    @Override
    public void updateViews() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
