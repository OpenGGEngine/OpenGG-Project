/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.window.awt.input;

import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.io.input.mouse.MouseButton;
import com.opengg.core.io.input.mouse.MouseButtonHandler;
import com.opengg.core.io.input.mouse.MouseController;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author Javier
 */
public class AWTMouseButtonHandler implements MouseListener, MouseButtonHandler {
    boolean[] buttons = new boolean[128];

    @Override
    public boolean isButtonDown(int button) {
        return false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(buttons[e.getButton()] == true) return;

        buttons[e.getButton()] = true;
        MouseController.buttonPressed(getEngineButtonCode(e.getButton()));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(buttons[e.getButton()] == false) return;

        buttons[e.getButton()] = false;
        MouseController.buttonReleased(getEngineButtonCode(e.getButton()));
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private static int getEngineButtonCode(int awtButton){
        return switch (awtButton){
            case MouseEvent.BUTTON1 -> MouseButton.LEFT;
            case MouseEvent.BUTTON2 -> MouseButton.MIDDLE;
            case MouseEvent.BUTTON3 -> MouseButton.RIGHT;
            default -> awtButton;
        };
    }
}
