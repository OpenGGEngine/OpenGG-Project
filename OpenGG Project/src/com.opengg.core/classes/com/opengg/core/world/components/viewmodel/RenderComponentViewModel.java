package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.Initializer;
import com.opengg.core.world.components.RenderComponent;

public class RenderComponentViewModel<T extends RenderComponent> extends ViewModel<T> {

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var renderDistance = new DataBinding.FloatBinding();
        renderDistance.name("Max Render Distance");
        renderDistance.internalName("renderDistance");
        renderDistance.setValueAccessorFromData(component::getRenderDistance);
        renderDistance.onViewChange(component::setRenderDistance);

        this.addElement(renderDistance);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public T getFromInitializer(Initializer init) {
        return null;
    }
}