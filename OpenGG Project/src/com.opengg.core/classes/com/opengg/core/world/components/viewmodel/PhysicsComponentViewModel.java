/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.Initializer;
import com.opengg.core.world.components.physics.PhysicsComponent;

/**
 *
 * @author Javier
 */
@ForComponent(PhysicsComponent.class)
public class PhysicsComponentViewModel extends ViewModel<PhysicsComponent>{

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var mass = new DataBinding.FloatBinding();
        mass.autoupdate = true;
        mass.name = "Mass";
        mass.internalname = "mass";
        mass.setValueAccessorFromData(() -> component.getEntity().mass);
        mass.onViewChange(s -> component.getEntity().mass = s);

        var density = new DataBinding.FloatBinding();
        density.autoupdate = true;
        density.name = "Density";
        density.internalname = "density";
        density.setValueAccessorFromData(() -> component.getEntity().density);
        density.onViewChange(s -> component.getEntity().density = s);

        var restitution = new DataBinding.FloatBinding();
        restitution.autoupdate = true;
        restitution.name = "Restitution";
        restitution.internalname = "restitution";
        restitution.setValueAccessorFromData(() -> component.getEntity().restitution);
        restitution.onViewChange(s -> component.getEntity().restitution = s);

        var sfriction = new DataBinding.FloatBinding();
        sfriction.autoupdate = true;
        sfriction.name = "Static friction";
        sfriction.internalname = "sfriction";
        sfriction.setValueAccessorFromData(() -> component.getEntity().staticfriction);
        sfriction.onViewChange(s -> component.getEntity().staticfriction = s);

        var dfriction = new DataBinding.FloatBinding();
        dfriction.autoupdate = true;
        dfriction.name = "Dynamic friction";
        dfriction.internalname = "dfriction";
        dfriction.setValueAccessorFromData(() -> component.getEntity().dynamicfriction);
        dfriction.onViewChange(s -> component.getEntity().dynamicfriction = s);

        addElement(mass);
        addElement(density);
        addElement(restitution);
        addElement(sfriction);
        addElement(dfriction);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public PhysicsComponent getFromInitializer(Initializer init) {
        return new PhysicsComponent();
    }

}
