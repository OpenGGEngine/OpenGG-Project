package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.world.components.RenderComponent;

public class RenderComponentViewModel<T extends RenderComponent> extends ComponentViewModel<T> {

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var renderDistance = new DataBinding.FloatBinding();
        renderDistance.name("Max Render Distance");
        renderDistance.internalName("renderDistance");
        renderDistance.setValueAccessorFromData(model::getRenderDistance);
        renderDistance.onViewChange(model::setRenderDistance);

        this.addElement(renderDistance);
    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {
        return init;
    }

    @Override
    public T getFromInitializer(BindingAggregate init) {
        return null;
    }
}