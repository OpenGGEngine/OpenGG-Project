/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.io.input.mouse;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector4f;
import com.opengg.core.physics.collision.PhysicsRay;
import com.opengg.core.render.RenderEngine;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class MouseController {
    private static Vector2f old = new Vector2f();
    private static double oldSX=0,oldSY=0;
    private static int counter;
    private static List<MouseButtonListener> buttonlisteners = new ArrayList<>();
    private static List<MouseMoveListener> poslisteners = new ArrayList<>();
    private static List<MouseScrollListener> scrollListeners = new ArrayList<>();
    private static MousePositionHandler poshandler;
    private static MouseButtonHandler buttonhandler;
    private static MouseScrollHandler scrollHandler;

    public static void clearMouseListeners() {
        buttonlisteners.clear();
        poslisteners.clear();
        scrollListeners.clear();
    }


    public static void addScrollListener(MouseScrollListener handle){
        scrollListeners.add(handle);
    }

    public static void setPosHandler(MousePositionHandler handle){
        poshandler = handle;
    }
    
    public static void setButtonHandler(MouseButtonHandler handle){
        buttonhandler = handle;
    }

    public static void setScrollHandler(MouseScrollHandler handle){scrollHandler = handle;}
    
    public static void onButtonPress(MouseButtonListener button){
        buttonlisteners.add(button);
    }

    public static void onMouseMove(MouseMoveListener move){
        poslisteners.add(move);
    }
    
    public static void buttonPressed(int key){
        for (var listener : buttonlisteners){
            listener.onButtonPress(key);
        }
    }
    
    public static void buttonReleased(int key){
        for (var listener : buttonlisteners){
            listener.onButtonRelease(key);
        }
    }

    public static PhysicsRay getRay(){
        Vector2f mouse = getRaw();
        return getRay(mouse.x,mouse.y);
    }

    public static PhysicsRay getRay(float xpos, float ypos){
        Vector2f mouse = getRaw();
        //For Cursor Lock
        //mouse = new Vector2f(OpenGG.getWindow().getWidth()/2,OpenGG.getWindow().getHeight()/2);
        float z = 1.0f;
        Vector3f ray_nds = new Vector3f((2*xpos)/OpenGG.getWindow().getWidth()-1.0f, 1-(ypos*2)/OpenGG.getWindow().getHeight(), z);
        Vector4f ray_clip = new Vector4f(ray_nds.x,ray_nds.y, -1.0f, 1.0f);
        Vector4f ray_eye = ray_clip.multiply(RenderEngine.getData().getMatrix().invert());
        ray_eye = new Vector4f(ray_eye.x,ray_eye.y, -1.0f, 0.0f);
        Vector3f ray_wor = ray_eye.multiply(RenderEngine.getCurrentView().getMatrix().invert()).truncate();
        ray_wor = ray_wor.normalize();

        PhysicsRay ray = new PhysicsRay(ray_wor, RenderEngine.getCurrentView().getPosition(), 1000f);
        return ray;
    }

    public static void update(){
        if(scrollHandler != null){
            if(oldSX != scrollHandler.getWheelX()||oldSY != scrollHandler.getWheelY()){
                for(var scrolll:scrollListeners){
                    scrolll.onScroll(scrollHandler.getWheelX(),scrollHandler.getWheelY());
                }
                oldSX = scrollHandler.getWheelX();
                oldSY = scrollHandler.getWheelY();
            }
        }

        if(!poshandler.getPos().equals(old)) {

            for (var move : poslisteners) {
                move.onMove(poshandler.getPos());
            }

            old = poshandler.getPos();
        }

    }
    
    public static boolean isButtonDown(int button){
        return buttonhandler.isButtonDown(button);
    }
    
    public static double getX(){
        return poshandler.getX();
    }
    
    public static double getY(){
        return poshandler.getY();
    }

    public static Vector2f get(){
        return poshandler.getPos().multiply(1f);
    }

    public static Vector2f getRaw(){
        return poshandler.getPos();
    }

    private MouseController() {
    }
}
