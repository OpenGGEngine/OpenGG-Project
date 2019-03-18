package com.opengg.core.world.components;

import com.opengg.core.audio.Soundtrack;
import com.opengg.core.audio.SoundtrackHandler;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;

public class SoundtrackComponent extends Component {
    Soundtrack soundtrack;

    public SoundtrackComponent() {
        soundtrack = new Soundtrack();
    }

    public SoundtrackComponent(String... songs){
        this();
        for(var song : songs){
            soundtrack.addSong(song);
        }
    }

    public Soundtrack getSoundtrack() {
        return soundtrack;
    }

    @Override
    public void onWorldEnable(){
        SoundtrackHandler.setSoundtrack(soundtrack);
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(soundtrack.getSongs().size());
        for(var song : soundtrack.getSongs()){
            out.write(song.getData().getSource());
        }
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException {
        super.deserialize(in);
        var amount = in.readInt();
        for(int i = 0; i < amount; i++){
            soundtrack.addSong(in.readString());
        }
    }

}
