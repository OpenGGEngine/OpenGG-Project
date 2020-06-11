/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.gui;

/**
 *
 * @author Javier
 */
public class GUI extends UIGroup{
    private boolean menu = false;

    public GUI() {}

    @Override
    public boolean isEnabled() {
        return enabled && GUIController.isEnabled();
    }

    @Override
    public void setParent(UIGroup parent) {
        throw new UnsupportedOperationException("Cannot add a root panel to another group");
    }

    public boolean isMenu() {
        return menu;
    }

    public void setMenu(boolean menu) {
        this.menu = menu;
    }
}
