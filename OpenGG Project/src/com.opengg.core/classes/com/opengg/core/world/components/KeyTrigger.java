/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.world.components.triggers.Trigger;
import com.opengg.core.world.components.triggers.TriggerInfo;
import static com.opengg.core.world.components.triggers.TriggerInfo.SINGLE;
import static com.opengg.core.world.components.triggers.TriggerInfo.TOGGLE;

/**
 *
 * @author Javier
 */
public class KeyTrigger extends Trigger implements KeyboardListener {

    int[] keys;
    
    public KeyTrigger(int... key){
        super();
        KeyboardController.addToPool(this);
        keys = key;
    }

    @Override
    public void keyPressed(int key) {
        for(int wkey : keys){
            if(key == wkey){
                TriggerInfo t = new TriggerInfo();
                t.info = Integer.toString(key);
                t.source = this;
                t.type = TOGGLE;
                trigger(t);
            }
        }
    }

    @Override
    public void keyReleased(int key) {
        
    }
    
}
