package com.opengg.core.world.components.viewmodel;

import com.opengg.core.audio.Sound;
import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.Initializer;
import com.opengg.core.world.components.SoundtrackComponent;

import java.util.Arrays;
import java.util.stream.Collectors;

@ForComponent(SoundtrackComponent.class)
public class SoundtrackComponentViewModel extends ViewModel<SoundtrackComponent>{
    @Override
    public void createMainViewModel() {
        DataBinding<String> tracks = new DataBinding.StringBinding();
        tracks.name("Track list (;)")
                .internalName("tracks")
                .autoUpdate(false)
                .onViewChange(s ->
                        Arrays.stream(((String)s).split(";"))
                                .map(Sound::new)
                                .forEach(ss -> component.getSoundtrack().addSong(ss)))
                .setValueAccessorFromData(() ->
                        component.getSoundtrack().getSongs().stream()
                                .map(s -> s.getData().getSource())
                                .collect(Collectors.joining(";")));

        this.addElement(tracks);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public SoundtrackComponent getFromInitializer(Initializer init) {
        return new SoundtrackComponent();
    }
}
