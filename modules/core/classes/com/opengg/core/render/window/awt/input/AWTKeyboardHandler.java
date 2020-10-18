/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.window.awt.input;

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


        KeyboardController.keyPressed(nkey);
        keys[nkey] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int nkey = e.getKeyCode();
        if(keys[nkey] == false) return;

        KeyboardController.keyReleased(nkey);
        keys[nkey] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
