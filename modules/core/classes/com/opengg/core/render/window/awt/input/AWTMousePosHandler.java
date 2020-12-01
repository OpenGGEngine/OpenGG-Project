/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.window.awt.input;

import com.opengg.core.io.input.mouse.MousePositionHandler;
import com.opengg.core.math.Vector2f;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author Javier
 */
public class AWTMousePosHandler implements MousePositionHandler, MouseMotionListener {
    double x, y;
    double lockXPos, lockYPos;

    boolean mouseLocked = false;

    Robot robot;

    public AWTMousePosHandler(){
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(!mouseLocked){
         //   x = e.getX();
         //   y = e.getY();
        }
    }

    public void updateLockMouse(){
        if(mouseLocked){
            x += MouseInfo.getPointerInfo().getLocation().x - lockXPos;
            y += MouseInfo.getPointerInfo().getLocation().y - lockYPos;
            robot.mouseMove((int)lockXPos, (int)lockYPos);
        }

    }

    public void setMouseLock(boolean mouseLock){
        if(mouseLock){
            lockXPos = MouseInfo.getPointerInfo().getLocation().x;
            lockYPos = MouseInfo.getPointerInfo().getLocation().y;
        }

        this.mouseLocked = mouseLock;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public Vector2f getPos() {
        return new Vector2f((float)x,(float)y);
    }
}
