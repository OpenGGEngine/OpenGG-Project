/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import com.opengg.core.engine.Resource;
import com.opengg.core.system.Allocator;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;

/**
 * Utility class to facilitate audio file loading
 * @author Javier
 */
public class AudioLoader {
    /**
     * Loads an Ogg Vorbis sound file from the given relative or absolute path, and returns loaded sound in a {@link SoundData} object
     * @param path Path to sound file, either relative or absolute
     * @return SoundData containing sound buffer and information
     * @throws IOException If file does not exist
     */
    public static SoundData loadVorbis(String path) throws IOException{
        IntBuffer samplerate= Allocator.stackAllocInt(1);
        IntBuffer channels = Allocator.stackAllocInt(1);

        var absolutePath = Resource.getAbsoluteFromLocal(path);

        if(!(new File(absolutePath).exists())){
            Allocator.popStack();
            Allocator.popStack();
            throw new IOException("Failed to find file at " + new File(path).getAbsolutePath());
        }
        ShortBuffer buffer = stb_vorbis_decode_filename(absolutePath, channels, samplerate);
        Allocator.register(buffer, Allocator.LWJGL_DEFAULT);
        
        SoundData data = new SoundData();
        data.channels = channels.get();
        data.samplerate = samplerate.get();
        data.format = data.channels == 2 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16;
        data.data = buffer;
        data.origin = path;
        
        Allocator.popStack();
        Allocator.popStack();
        
        return data;
    }

    private AudioLoader() {
    }
}
