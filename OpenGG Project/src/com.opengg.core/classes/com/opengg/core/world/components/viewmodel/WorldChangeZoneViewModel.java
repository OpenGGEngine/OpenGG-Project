package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.world.components.WorldChangeZone;


@ForComponent(WorldChangeZone.class)
public class WorldChangeZoneViewModel extends ZoneViewModel<WorldChangeZone> {

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var destWorld = new DataBinding.StringBinding();
        destWorld.name("Destination World").internalName("destWorld");
        destWorld.setValueAccessorFromData(model::getTargetWorld);
        destWorld.onViewChange(model::setWorld);
        this.addElement(destWorld);
    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {
        return init;
    }

    @Override
    public WorldChangeZone getFromInitializer(BindingAggregate init) {
        return new WorldChangeZone();
    }
}