/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.audio.Sound;
import com.opengg.core.engine.Resource;
import com.opengg.core.world.components.triggers.Trigger;
import com.opengg.core.world.components.triggers.TriggerInfo;
import com.opengg.core.world.components.triggers.Triggerable;

/**
 *
 * @author Javier
 */
public class SoundComponent extends Component implements Triggerable, ResourceUser {
    private Sound sound;
    
    public SoundComponent(){
        super();
    }

    public SoundComponent(Sound sound){
        super();
        this.sound = sound;
    }

    public Sound getSound(){
        return sound;
    }

    @Override
    public void onTrigger(Trigger source, TriggerInfo data) {
        sound.stop();
        sound.play();
    }

    @Override
    public Resource getResource(){
        return sound.getData();
    }
}
