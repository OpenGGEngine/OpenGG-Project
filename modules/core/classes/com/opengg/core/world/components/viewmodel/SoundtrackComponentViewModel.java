package com.opengg.core.world.components.viewmodel;

import com.opengg.core.audio.Sound;
import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.world.components.SoundtrackComponent;

import java.util.Arrays;
import java.util.stream.Collectors;

@ForComponent(SoundtrackComponent.class)
public class SoundtrackComponentViewModel extends ComponentViewModel<SoundtrackComponent> {
    @Override
    public void createMainViewModel() {
        DataBinding<String> tracks = new DataBinding.StringBinding();
        tracks.name("Track list (;)")
                .internalName("tracks")
                .autoUpdate(false)
                .onViewChange(s ->
                        Arrays.stream(((String)s).split(";"))
                                .map(Sound::new)
                                .forEach(ss -> model.getSoundtrack().addSong(ss)))
                .setValueAccessorFromData(() ->
                        model.getSoundtrack().getSongs().stream()
                                .map(s -> s.getData().getSource())
                                .collect(Collectors.joining(";")));

        this.addElement(tracks);
    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {
        return init;
    }

    @Override
    public SoundtrackComponent getFromInitializer(BindingAggregate init) {
        return new SoundtrackComponent();
    }
}
