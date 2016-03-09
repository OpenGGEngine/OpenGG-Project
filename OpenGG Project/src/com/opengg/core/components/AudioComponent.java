/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.components;

import com.opengg.core.audio.AudioHandler;
import com.opengg.core.audio.AudioSource;
import java.net.URL;

/**
 *
 * @author Javier
 */
public class AudioComponent implements Updatable, Triggerable {
    
    AudioSource so;
    
    public void AudioComponent(URL u){
        int s1 = AudioHandler.loadSound(u);
        so = new AudioSource(s1);
    }
    
    @Override
    public void update(float delta) {
        
    }

    @Override
    public void onTrigger() {
        so.play();
    }
    
    public void setSound(){
        
    }
}
