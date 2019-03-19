package com.opengg.core.world.components.viewmodel;

import com.opengg.core.audio.Sound;
import com.opengg.core.world.components.SoundtrackComponent;

import java.util.Arrays;
import java.util.stream.Collectors;

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
        if(element.internalname.equals("tracks")) {
            var songs = Arrays.stream(((String) element.value).split(";"))
                    .map(Sound::new)
                    .collect(Collectors.toList());
            if(!songs.isEmpty())
                songs.forEach(s -> component.getSoundtrack().addSong(s));
        }

    }

    @Override
    public void updateView(Element element) {

    }
}
