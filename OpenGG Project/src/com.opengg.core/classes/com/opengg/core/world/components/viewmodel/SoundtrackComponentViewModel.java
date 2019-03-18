package com.opengg.core.world.components.viewmodel;

import com.opengg.core.world.components.SoundtrackComponent;

@ForComponent(SoundtrackComponent.class)
public class SoundtrackComponentViewModel extends ViewModel<SoundtrackComponent>{
    @Override
    public void createMainViewModel() {
        addElement(new Element()
                    .name("Track list (;)")
                    .value("")
                    .internalName("tracks")
                    .type(Element.Type.STRING)
                    .autoUpdate(false));

    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public SoundtrackComponent getFromInitializer(Initializer init) {
        return new SoundtrackComponent();
    }

    @Override
    public void onChange(Element element) {

    }

    @Override
    public void updateView(Element element) {

    }
}
