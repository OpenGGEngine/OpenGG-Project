/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.audio.openal;

import com.opengg.core.GGInfo;
import com.opengg.core.audio.openal.ALBuffer;
import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Vector3f;
import org.lwjgl.openal.AL10;

import static org.lwjgl.openal.AL10.*;


/**
 * Low abstraction, almost 1 to 1 object version of an OpenAL sound source
 * @author Warren
 */
public final class NativeSound {
    /**
     * OpenAL sound handle
     */
    private final int audioid;
    

    public NativeSound(){
        if(GGInfo.isServer()) {
            audioid = -1;
            return;
        }

        audioid = alGenSources();

        int i = AL10.alGetError();
        if(i != AL10.AL_NO_ERROR){
            GGConsole.error("OpenAL Error in AudioSource Generation: " + i);
            remove();
        }
    }

    /**
     * Creates an OpenAL sound source and fills it with the given {@link ALBuffer} <br>
     * Note, this calls OpenAL commands so an OpenAL context must already exist.
     * Additionally, if the sound fails to load it automatically deletes and frees the source
     * @param buffer ALBuffer containing a valid sound
     */
    public NativeSound(ALBuffer buffer){
        this();
        this.setBuffer(buffer);
    }
    
    /**
     * Plays the source
     */
    public void play(){
        if(GGInfo.isServer()) return;
        alSourcePlay(audioid);
    }
    
    /**
     * Pauses the source
     */
    public void pause(){
        if(GGInfo.isServer()) return;
        alSourcePause(audioid);
    }
    
    /**
     * Stops the source
     */
    public void stop(){
        if(GGInfo.isServer()) return;
        alSourceStop(audioid);
    }
    
    /**
     * Rewinds the source
     */
    public void rewind(){
        if(GGInfo.isServer()) return;
        alSourceRewind(audioid);
    }
    
    /**
     * Sets if the source position should be relative to the listener
     * @param relative If sound should be relative
     */
    public void setRelativePosition(boolean relative){
        if(GGInfo.isServer()) return;
        alSourcei(audioid, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
    }
    
    /**
     * Loads the {@link ALBuffer} into the source
     * @param buffer ALBuffer to load in
     */
    public void setBuffer(ALBuffer buffer){
        if(GGInfo.isServer()) return;
        alSourcei(audioid, AL_BUFFER, buffer.getId());
    } 
    
    /**
     * Sets if the source should loop when it ends
     * @param loop If source should loop
     */
    public void setLoop(boolean loop){
        if(GGInfo.isServer()) return;
        alSourcei(audioid, AL10.AL_LOOPING,  loop ? AL10.AL_TRUE : AL10.AL_FALSE  );
    }

    /**
     * Sets source gain
     * @param s Gain
     */
    public void setGain(float s){
        if(GGInfo.isServer()) return;
        alSourcef(audioid, AL_GAIN,s);
    }
    
    /**
     * Sets source position, either relative or absolute depending on the state set by {@link #setRelativePosition}
     * @param pos Position
     */
    public void setPosition(Vector3f pos){
        if(GGInfo.isServer()) return;
        alSource3f(audioid, AL_POSITION, pos.x, pos.y, pos.z);
    }
    
    /**
     * Sets source velocity, used if Doppler effect is enabled
     * @param vel Velocity
     */
    public void setVelocity(Vector3f vel){
        if(GGInfo.isServer()) return;
        alSource3f(audioid, AL_VELOCITY, vel.x, vel.y, vel.z);
    }
    
    /**
     * Sets source direction
     * @param dir Direction
     */
    public void setDirection(Vector3f dir){
        if(GGInfo.isServer()) return;
        alSource3f(audioid, AL_DIRECTION, dir.x, dir.z, dir.z);
    }
    
    /**
     * Sets source sound pitch offset
     * @param pitch Pitch offset
     */
    public void setPitch(float pitch){
        if(GGInfo.isServer()) return;
        alSourcef(audioid, AL_PITCH, pitch);
    }
    
    /**
     * Sets the maximum distance at which the source can be heard, also changes speed of fadeout
     * @param dist Maximum distance
     */
    public void setMaxDistance(float dist){
        if(GGInfo.isServer()) return;
        alSourcef(audioid, AL_MAX_DISTANCE, dist);
    }
    
    /**
     * Changes how quickly the sound fades out
     * @param factor Fade out speed
     */
    public void setRolloffFactor(float factor){
        if(GGInfo.isServer()) return;
        alSourcef(audioid, AL_ROLLOFF_FACTOR, factor);
    }
    
    /**
     * Sets the minimum gain for this sound, will not go lower until it stops being heard at the maximum distance 
     * @param gain Minimum gain
     */
    public void setMinimumGain(float gain){
        if(GGInfo.isServer()) return;
        alSourcef(audioid, AL_MIN_GAIN, gain);
    }
    
    /**
     * Sets the maximum gain for this sound, will not go higher even if the source is on the listener
     * @param gain maximum gain
     */

    public void setMaximumGain(float gain){
        if(GGInfo.isServer()) return;
        alSourcef(audioid, AL_MAX_GAIN, gain);
    }
    
    /**
     * Returns the playing state of the source
     * @return State of source
     */
    public int getState(){
        if(GGInfo.isServer()) return 0;
        return alGetSourcei(audioid, AL_SOURCE_STATE);
    }
    
    /**
     * Returns the source handle, or -1 if the source has been destroyed
     * @return Source handle
     */
    public int getID(){
        return audioid;
    }
    
    /**
     * Removes the source and frees Resource used
     */
    public void remove(){
        if(GGInfo.isServer()) return;
        alDeleteSources(audioid);
    } 
}
