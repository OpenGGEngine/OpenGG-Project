/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.world.components.physics.RigidBodyComponent;

/**
 *
 * @author Javier
 */
@ForComponent(RigidBodyComponent.class)
public class RigidBodyComponentViewModel extends ComponentViewModel<RigidBodyComponent> {

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var restitution = new DataBinding.FloatBinding();
        restitution.autoupdate = true;
        restitution.name = "Restitution";
        restitution.internalname = "restitution";
        restitution.setValueAccessorFromData(() -> model.getRigidBody().restitution);
        restitution.onViewChange(s -> model.getRigidBody().restitution = s);

        var sfriction = new DataBinding.FloatBinding();
        sfriction.autoupdate = true;
        sfriction.name = "Static friction";
        sfriction.internalname = "sfriction";
        sfriction.setValueAccessorFromData(() -> model.getRigidBody().staticfriction);
        sfriction.onViewChange(s -> model.getRigidBody().staticfriction = s);

        var dfriction = new DataBinding.FloatBinding();
        dfriction.autoupdate = true;
        dfriction.name = "Dynamic friction";
        dfriction.internalname = "dfriction";
        dfriction.setValueAccessorFromData(() -> model.getRigidBody().dynamicfriction);
        dfriction.onViewChange(s -> model.getRigidBody().dynamicfriction = s);

        addElement(restitution);
        addElement(sfriction);
        addElement(dfriction);

        if(model.getRigidBody().hasPhysicsProvider()){

            var mass = new DataBinding.FloatBinding();
            mass.autoupdate = true;
            mass.name = "Mass";
            mass.internalname = "mass";
            mass.setValueAccessorFromData(() -> model.getRigidBody().getPhysicsProvider().get().mass);
            mass.onViewChange(s -> model.getRigidBody().getPhysicsProvider().get().mass = s);

            var density = new DataBinding.FloatBinding();
            density.autoupdate = true;
            density.name = "Density";
            density.internalname = "density";
            density.setValueAccessorFromData(() -> model.getRigidBody().getPhysicsProvider().get().density);
            density.onViewChange(s -> model.getRigidBody().getPhysicsProvider().get().density = s);


            addElement(mass);
            addElement(density);
        }

    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {
        return init;
    }

    @Override
    public RigidBodyComponent getFromInitializer(BindingAggregate init) {
        return new RigidBodyComponent();
    }

}
