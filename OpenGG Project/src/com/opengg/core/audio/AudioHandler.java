/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.audio;

import com.opengg.core.Vector3f;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALContext;
import org.lwjgl.openal.ALDevice;

/**
 *
 * @author Javier
 */
public class AudioHandler {
    static IntBuffer buffer = BufferUtils.createIntBuffer(1),source = BufferUtils.createIntBuffer(1);
    FloatBuffer sourcePos = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
    FloatBuffer sourceVel = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
    FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
    FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });   
    FloatBuffer listenerOri =
    BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f });
    public static void init(int window){
        ALContext context = ALContext.create();
        ALDevice device = context.getDevice();

        context.makeCurrent();

        ALCCapabilities capabilities = device.getCapabilities();
        AL10.alGenSources(source);
        if (!capabilities.OpenALC10)
            throw new RuntimeException("OpenAL Context Creation failed");
        AL10.alEnable(window);
        AL10.alSourcei(source.get(0), AL10.AL_BUFFER,buffer.get(0) );
        AL10.alSourcef(source.get(0), AL10.AL_PITCH,1f );
        AL10.alSourcef(source.get(0), AL10.AL_GAIN,0.5f);
        AL10.alSource3f (source.get(0), AL10.AL_POSITION, 0,0,0);
        AL10.alSource3f (source.get(0), AL10.AL_VELOCITY, 0,0,0);
        
        AL10.alListener3f(AL10.AL_POSITION,0,0,0);
        AL10.alListener3f(AL10.AL_VELOCITY,0,0,0);
        AL10.alListener3f(AL10.AL_ORIENTATION,0,0,0);
    }
    
    public static void setSoundBuffer(URL sound){
        AL10.alGenBuffers(buffer);
 
        if(AL10.alGetError() != AL10.AL_NO_ERROR)
            System.out.println(AL10.alGetError());
        WaveData waveFile = WaveData.create(sound);
        AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
        waveFile.dispose();

        AL10.alSourcei(source.get(0), AL10.AL_BUFFER,buffer.get(0) );
 
        if (AL10.alGetError() != AL10.AL_NO_ERROR)
          System.out.println(AL10.alGetError());
    }
    
    public static void play(){
        AL10.alSourcePlay(source.get(0));
    }
    
    public static void destroy(){
            AL10.alDeleteSources(source);
            AL10.alDeleteBuffers(buffer);
    }
    
    public static void shouldLoop(boolean loop){
        if(loop){
            AL10.alSourcei(source.get(0), AL10.AL_LOOPING,  AL10.AL_TRUE  );
        }else{
            AL10.alSourcei(source.get(0), AL10.AL_LOOPING,  AL10.AL_FALSE  );
        }
    }
    
    public static void setListenerPos(Vector3f pos){
        AL10.alListener3f(AL10.AL_POSITION,pos.x,pos.y,pos.z);
    }
}
