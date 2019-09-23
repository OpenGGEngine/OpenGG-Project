package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.Initializer;
import com.opengg.core.world.components.WorldChangeZone;


@ForComponent(WorldChangeZone.class)
public class WorldChangeZoneViewModel extends ZoneViewModel<WorldChangeZone> {

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var destWorld = new DataBinding.StringBinding();
        destWorld.name("Destination World").internalName("destWorld");
        destWorld.setValueAccessorFromData(component::getTargetWorld);
        destWorld.onViewChange(component::setWorld);
        this.addElement(destWorld);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public WorldChangeZone getFromInitializer(Initializer init) {
        return new WorldChangeZone();
    }
}