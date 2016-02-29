/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.audio;

import org.lwjgl.openal.AL10;
import static org.lwjgl.openal.AL10.*;


/**
 *
 * @author Warren
 */
public class AudioSource {
    private int audioid;
    
    public AudioSource(){
        audioid = alGenSources();
        alSourcef(audioid,AL_GAIN,1);
        alSourcef(audioid,AL_PITCH,1);
        alSource3f(audioid,AL_POSITION,0,0,0);
    }
    public void play(int bufferid){
        alSourcei(audioid,AL_BUFFER,bufferid);
        alSourcePlay(audioid);
    }
    public void setShouldLoop(boolean loop){
        if(loop){
            AL10.alSourcei(audioid, AL10.AL_LOOPING,  AL10.AL_TRUE  );
        }else{
            AL10.alSourcei(audioid, AL10.AL_LOOPING,  AL10.AL_FALSE  );
        }
    }
    public void remove(){
        alDeleteSources(audioid);
    }
}
