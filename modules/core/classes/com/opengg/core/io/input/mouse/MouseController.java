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
    private static final List<MouseButtonListener> buttonlisteners = new ArrayList<>();
    private static final List<MouseMoveListener> poslisteners = new ArrayList<>();
    private static final List<MouseScrollListener> scrollListeners = new ArrayList<>();
    private static final List<MouseScrollChangeListener> scrollChangeListeners = new ArrayList<>();
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

    public static void addScrollChangeListener(MouseScrollChangeListener handle){
        scrollChangeListeners.add(handle);
    }

    public static void setPosHandler(MousePositionHandler handle){
        poshandler = handle;
    }
    
    public static void setButtonHandler(MouseButtonHandler handle){
        buttonhandler = handle;
    }

    public static void setScrollHandler(MouseScrollHandler handle){scrollHandler = handle;}
    
    public static void onButtonPress(MouseButtonListener button){
        OpenGG.asyncExec(() -> buttonlisteners.add(button));
    }

    public static void removeButtonListener(MouseButtonListener button){
        OpenGG.asyncExec(() -> buttonlisteners.remove(button));
    }

    public static void onMouseMove(MouseMoveListener move){
        poslisteners.add(move);
    }
    
    public static void buttonPressed(int key){
        for (var listener : List.copyOf(buttonlisteners)){
            listener.onButtonPress(key);
        }
    }
    
    public static void buttonReleased(int key){
        for (var listener : List.copyOf(buttonlisteners)){
            listener.onButtonRelease(key);
        }
    }

    public static void scrolledUp(){
        scrollChangeListeners.forEach(MouseScrollChangeListener::onScrollUp);
    }

    public static void scrolledDown(){
        scrollChangeListeners.forEach(MouseScrollChangeListener::onScrollDown);
    }

    public static PhysicsRay getRay(){
        return getRay(getRaw());
    }

    public static PhysicsRay getRay(Vector2f pos){
        float z = 1.0f;
        Vector3f ray_nds = new Vector3f((2*pos.x)/OpenGG.getWindow().getWidth()-1.0f, 1-(pos.y*2)/OpenGG.getWindow().getHeight(), z);
        Vector4f ray_clip = new Vector4f(ray_nds.x,ray_nds.y, -1.0f, 1.0f);
        Vector4f ray_eye = ray_clip.multiply(RenderEngine.getProjectionData().getMatrix().invert());
        ray_eye = new Vector4f(ray_eye.x,ray_eye.y, -1.0f, 0.0f);
        Vector3f ray_wor = ray_eye.multiply(RenderEngine.getCurrentView().getMatrix().invert()).truncate();
        ray_wor = ray_wor.normalize();

        PhysicsRay ray = new PhysicsRay(ray_wor, RenderEngine.getCurrentView().getPosition(), 1000f);
        return ray;
    }

    public static void update(){
        if(scrollHandler != null){
            if(oldSX != scrollHandler.getWheelX() || oldSY != scrollHandler.getWheelY()){
                for(var scrolll:scrollListeners){
                    scrolll.onScroll(scrollHandler.getWheelX(), scrollHandler.getWheelY());
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
        return poshandler.getPos();
    }

    public static Vector2f getInScreenspace(){
        var invertedPos = poshandler.getPos().divide(new Vector2f(OpenGG.getWindow().getWidth(), OpenGG.getWindow().getHeight()));
        return new Vector2f(invertedPos.x, 1-invertedPos.y);
    }

    public static Vector2f getRaw(){
        return poshandler.getRawPos();
    }

    private MouseController() {
    }
}
