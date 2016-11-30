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
public class MouseButtonEventHandler {
    static int counter;
    static MouseButtonListener[] k = new MouseButtonListener[16];;
    
    public static void addToPool(MouseButtonListener ks){
        k[counter] = ks;
        counter++;
        
    }
    
    public static void keyPressed(int key){
        try{
            for (MouseButtonListener k1 : k) {
                k1.buttonPressed(key);
            }
        }catch(NullPointerException e){
            
        }
    }
    public static void keyReleased(int key){
        try{
            for (MouseButtonListener k1 : k) {
                k1.buttonReleased(key);
            }
        }catch(NullPointerException e){
            
        }
    }
}
