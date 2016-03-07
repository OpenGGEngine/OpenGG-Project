/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.audio;

import com.opengg.core.Vector3f;
import org.lwjgl.openal.AL10;
import static org.lwjgl.openal.AL10.*;


/**
 *
 * @author Warren
 */
public class AudioSource {
    private int audioid;
    private int bufferid;
    boolean isPaused = true;
    public AudioSource(int bufferid){
        this.bufferid = bufferid;
        audioid = alGenSources();
        alSourcei(audioid,AL_BUFFER,bufferid);
        alSourcef(audioid,AL_GAIN,1);
        alSourcef(audioid,AL_PITCH,1);
        alSource3f(audioid,AL_POSITION,0,0,0);
    }
    public void play(){
        isPaused = false;
        alSourcePlay(audioid);
    }
    public void pause(){
        isPaused = true;
        alSourcePause(audioid);
    }
    public void setShouldLoop(boolean loop){
        if(loop){
            AL10.alSourcei(audioid, AL10.AL_LOOPING,  AL10.AL_TRUE  );
        }else{
            AL10.alSourcei(audioid, AL10.AL_LOOPING,  AL10.AL_FALSE  );
        }
    }
    
    public boolean isPaused(){
        return isPaused;
    }
    
    public void remove(){
        alDeleteSources(audioid);
    } 
    public void setGain(float s){
        alSourcef(audioid,AL_GAIN,s);
    }
    public void setPosition(Vector3f pos){
        alSource3f(audioid,AL_POSITION,pos.x,pos.y,pos.z);
    }
    public void setVelocity(Vector3f vel){
        alSource3f(audioid,AL_VELOCITY,vel.x,vel.y,vel.z);
    }
}
