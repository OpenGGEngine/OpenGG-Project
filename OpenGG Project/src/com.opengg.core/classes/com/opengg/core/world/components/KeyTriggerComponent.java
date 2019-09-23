/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.world.components.triggers.TriggerInfo;

/**
 *
 * @author Javier
 */
public class KeyTriggerComponent extends TriggerComponent implements KeyboardListener {

    int[] keys;
    
    public KeyTriggerComponent(int... key){
        super();
        KeyboardController.addKeyboardListener(this);
        keys = key;
    }

    @Override
    public void keyPressed(int key) {
        for(int wkey : keys){
            if(key == wkey){
                TriggerInfo t = new TriggerInfo();
                t.info = Integer.toString(key);
                t.triggerSource = this;
                t.type = TriggerInfo.TriggerType.TOGGLE;
                trigger(t);
            }
        }
    }

    @Override
    public void keyReleased(int key) {
        
    }
    
}
