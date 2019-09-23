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
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.Zone;
import com.opengg.core.world.components.triggers.Triggerable;

import java.util.Arrays;
import java.util.stream.Collectors;

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
        lwh.setValueAccessorFromData(() -> component.getBox().getLWH());
        lwh.onViewChange(v -> component.setBox(new AABB(v)));

        addElement(lwh);

    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public T getFromInitializer(Initializer init) {
        return (T) new Zone();
    }
}
