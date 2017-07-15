/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.io.input.mouse;

/**
 *
 * @author Javier
 */
public class MouseController {
    static int counter;
    static MouseButtonListener[] k = new MouseButtonListener[16];
    static IMousePosHandler handler;
    static IMouseButtonHandler bhandler;
    
    public static void setPosHandler(IMousePosHandler handle){
        handler = handle;
    }
    
    public static void setButtonHandler(IMouseButtonHandler handle){
        bhandler = handle;
    }
    
    public static void addToPool(MouseButtonListener ks){
        k[counter] = ks;
        counter++;
        
    }
    
    public static void buttonPressed(int key){
        try{
            for (MouseButtonListener k1 : k) {
                k1.buttonPressed(key);
            }
        }catch(NullPointerException e){
            
        }
    }
    
    public static void buttonReleased(int key){
        try{
            for (MouseButtonListener k1 : k) {
                k1.buttonReleased(key);
            }
        }catch(NullPointerException e){
            
        }
    }
    
    public static boolean isButtonDown(int button){
        return bhandler.isButtonDown(button);
    }
    
    public static double getX(){
        return handler.getX();
    }
    
    public static double getY(){
        return handler.getY();
    }
}
