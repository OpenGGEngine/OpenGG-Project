/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class SoundEffect {
    List<Sound> sounds = new ArrayList<>();
    ALBuffer buffer;
    
    public SoundEffect(SoundData data){
        buffer = new ALBuffer(data);
    }
}
