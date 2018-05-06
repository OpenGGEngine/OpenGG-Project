/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.io.input.mouse;

import com.opengg.core.math.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class MouseController {
    private static Vector2f old = new Vector2f();
    private static int counter;
    private static List<MouseButtonListener> buttonlisteners = new ArrayList<>();
    private static List<MouseMoveListener> poslisteners = new ArrayList<>();
    private static MousePositionHandler poshandler;
    private static MouseButtonHandler buttonhandler;
    
    public static void setPosHandler(MousePositionHandler handle){
        poshandler = handle;
    }
    
    public static void setButtonHandler(MouseButtonHandler handle){
        buttonhandler = handle;
    }
    
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
            listener.onButtonPress(key);
        }
    }

    public static void update(){
        if(poshandler.getPos().equals(old))
            return;

        for(var move: poslisteners){
            move.onMove(poshandler.getPos());
        }

        old = poshandler.getPos();
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
