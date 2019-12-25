/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.physics.collision.colliders.AABB;
import com.opengg.core.world.components.Zone;

/**
 * @author Javier
 */
@ForComponent(Zone.class)
public class ZoneViewModel<T extends Zone> extends TriggerComponentViewModel<T> {

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var lwh = new DataBinding.Vector3fBinding();
        lwh.autoupdate = true;
        lwh.name = "LWH";
        lwh.internalname = "lwh";
        lwh.visible = true;
        lwh.setValueAccessorFromData(() -> model.getBox().getLWH());
        lwh.onViewChange(v -> model.setBox(new AABB(v)));

        addElement(lwh);

    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {
        return init;
    }

    @Override
    public T getFromInitializer(BindingAggregate init) {
        return (T) new Zone();
    }
}
