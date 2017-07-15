/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.io.input.keyboard;

/**
 *
 * @author Javier
 */
public class KeyboardHandler implements IKeyboardHandler{
    
    public boolean[] keys = new boolean[1024];
    
    @Override
    public boolean isKeyDown(int key) {
        return keys[key];
    }
    
}
