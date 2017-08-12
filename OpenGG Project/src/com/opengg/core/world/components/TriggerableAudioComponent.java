/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.audio.Sound;
import com.opengg.core.world.components.triggers.Trigger;
import com.opengg.core.world.components.triggers.TriggerInfo;
import com.opengg.core.world.components.triggers.Triggerable;

/**
 *
 * @author Javier
 */
public class TriggerableAudioComponent extends Component implements Triggerable {
    Sound s;
    
    public TriggerableAudioComponent(){
        super();
    }
    public TriggerableAudioComponent(Sound s){
        super();
        this.s = s;
    }
    
    @Override
    public void onTrigger(Trigger source, TriggerInfo data) {
        s.stop();
        s.play();
    }

    @Override
    public void onSubscribe(Trigger trigger) {}
}
