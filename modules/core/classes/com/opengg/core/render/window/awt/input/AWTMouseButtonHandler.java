/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.window.awt.input;

import com.opengg.core.io.input.mouse.MouseButtonHandler;

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
        buttons[e.getButton()] = true;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        buttons[e.getButton()] = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
