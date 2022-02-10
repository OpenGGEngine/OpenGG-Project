/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.window.awt.input;

import com.opengg.core.io.input.mouse.MousePositionHandler;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.window.awt.window.GGCanvas;
import com.opengg.core.util.SystemUtil;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;

/**
 *
 * @author Javier
 */
public class AWTMousePosHandler implements MousePositionHandler, MouseMotionListener {
    private Component parent;

    double x, y;
    private double lockXPos, lockYPos;

    private double compatXOffset, compatYOffset;

    private boolean mouseLocked = false;
    private boolean emulatedLock = false;

    private Robot robot;

    public AWTMousePosHandler(Component parent){
        this.parent = parent;
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
        if(mouseLocked && !emulatedLock){
            x += MouseInfo.getPointerInfo().getLocation().x - lockXPos;
            y += MouseInfo.getPointerInfo().getLocation().y - lockYPos;
            robot.mouseMove((int)lockXPos, (int)lockYPos);
        } else if(mouseLocked) { //wayland compat
            x += MouseInfo.getPointerInfo().getLocation().x - compatXOffset;
            y += MouseInfo.getPointerInfo().getLocation().y - compatYOffset;

            compatXOffset = MouseInfo.getPointerInfo().getLocation().x ;
            compatYOffset = MouseInfo.getPointerInfo().getLocation().y;
        }
    }

    public void setMouseLock(boolean mouseLock){
        this.mouseLocked = mouseLock;
        this.emulatedLock = mouseLock && SystemUtil.WINDOW_SESSION_HOST == SystemUtil.WindowSessionType.WAYLAND;

        System.out.println(emulatedLock);

        if (mouseLock) {
            lockXPos = MouseInfo.getPointerInfo().getLocation().x;
            lockYPos = MouseInfo.getPointerInfo().getLocation().y;
        }

        if (emulatedLock) {
            compatXOffset = MouseInfo.getPointerInfo().getLocation().x;
            compatYOffset = MouseInfo.getPointerInfo().getLocation().y;
        }
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

    @Override
    public Vector2f getRawPos() {
        var loc = parent.getLocationOnScreen();

        loc.x *= GGCanvas.osScaleFactor.x;
        loc.y *= GGCanvas.osScaleFactor.y;

        return new Vector2f((float)MouseInfo.getPointerInfo().getLocation().x * GGCanvas.osScaleFactor.x - loc.x,(float)MouseInfo.getPointerInfo().getLocation().y * GGCanvas.osScaleFactor.y - loc.y);
    }
}
