/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io.input;

/**
 *
 * @author Javier
 */
public class KeyboardEventHandler {
    static int counter;
    static KeyboardListener[] k = new KeyboardListener[16];;
    
    public static void addToPool(KeyboardListener ks){
        k[counter] = ks;
        counter++;
        
    }
    
    public static void keyPressed(int key){
        try{
            for (KeyboardListener k1 : k) {
                k1.keyPressed(key);
            }
        }catch(NullPointerException e){
            
        }
    }
    public static void keyReleased(int key){
        try{
            for (KeyboardListener k1 : k) {
                k1.keyReleased(key);
            }
        }catch(NullPointerException e){
            
        }
    }
    
}
