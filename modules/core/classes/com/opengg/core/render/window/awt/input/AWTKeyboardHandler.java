/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.window.awt.input;

import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardHandler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 *
 * @author Javier
 */
public class AWTKeyboardHandler extends KeyboardHandler implements KeyListener {

    @Override
    public void keyPressed(KeyEvent e) {
        int nkey = e.getKeyCode();
        if(keys[nkey] == true) return;

        KeyboardController.keyPressed(getEngineKeyCode(nkey));
        keys[nkey] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int nkey = e.getKeyCode();
        if(keys[nkey] == false) return;

        KeyboardController.keyReleased(getEngineKeyCode(nkey));
        keys[nkey] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    private static int getEngineKeyCode(int awtKey){
        return switch (awtKey){
            case KeyEvent.VK_SHIFT -> Key.KEY_LEFT_SHIFT;
            case KeyEvent.VK_CONTROL -> Key.KEY_LEFT_CONTROL;
            case KeyEvent.VK_ALT -> Key.KEY_LEFT_ALT;
            case KeyEvent.VK_TAB -> Key.KEY_TAB;
            case KeyEvent.VK_ENTER -> Key.KEY_ENTER;
            default -> awtKey;
        };
    }
}
