/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.audio;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.math.Vector3f;
import org.lwjgl.openal.AL10;
import static org.lwjgl.openal.AL10.*;


/**
 *
 * @author Warren
 */
public final class NativeSound {
    private int audioid;
    
    public NativeSound(ALBuffer buffer){  
        audioid = alGenSources();
        setBuffer(buffer);
        setMinimumGain(0f);
        setMaximumGain(1f);
        setPosition(new Vector3f());
        setVelocity(new Vector3f());
        
        int i = AL10.alGetError();
        if(i != AL10.AL_NO_ERROR){
            GGConsole.error("OpenAL Error in AudioSource Generation: " + i);
            remove();
        }
    }
    
    public void play(){
        alSourcePlay(audioid);
    }
    
    public void pause(){
        alSourcePause(audioid);
    }
    
    public void stop(){
        alSourceStop(audioid);
    }
    
    public void rewind(){
        alSourceRewind(audioid);
    }
    
    public void setRelativePosition(boolean relative){
        alSourcei(audioid, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
    }
    
    public void setBuffer(ALBuffer buffer){
        alSourcei(audioid, AL_BUFFER, buffer.id);
    } 
    
    public void setLoop(boolean loop){
        alSourcei(audioid, AL10.AL_LOOPING,  loop ? AL10.AL_TRUE : AL10.AL_FALSE  );
    }

    public void setGain(float s){
        alSourcef(audioid, AL_GAIN,s);
    }
    
    public void setPosition(Vector3f pos){
        alSource3f(audioid, AL_POSITION, pos.x(), pos.y(), pos.z());
    }
    
    public void setVelocity(Vector3f vel){
        alSource3f(audioid, AL_VELOCITY, vel.x(), vel.y(), vel.z());
    }
    
    public void setDirection(Vector3f dir){
        alSource3f(audioid, AL_DIRECTION, dir.x(), dir.z(), dir.z());
    }
    
    public void setPitch(float pitch){
        alSourcef(audioid, AL_PITCH, pitch);
    }
    
    public void setMaxDistance(float dist){
        alSourcef(audioid, AL_MAX_DISTANCE, dist);
    }
    
    public void setRolloffFactor(float factor){
        alSourcef(audioid, AL_ROLLOFF_FACTOR, factor);
    }
    
    public void setMinimumGain(float gain){
        alSourcef(audioid, AL_MIN_GAIN, gain);
    }
    
    public void setMaximumGain(float gain){
        alSourcef(audioid, AL_MAX_GAIN, gain);
    }
    
    public int getState(){
        return alGetSourcei(audioid, AL_SOURCE_STATE);
    }
    
    public int getID(){
        return audioid;
    }
    
    public void remove(){
        alDeleteSources(audioid);
    } 
}
