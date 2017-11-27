/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io.input.keyboard;

/**
 *
 * @author Javier
 */
public class KeyboardController {
    static int counter;
    static IKeyboardHandler handler;
    static KeyboardListener[] k = new KeyboardListener[32];;
    
    public static int charToKeycode(char ch){
        if(Character.isAlphabetic(ch))
            return (int)Character.toUpperCase(ch);
        else return (int)ch;
            
    }
    
    public static char keycodeToChar(int key){
        return (char)key;
    }
    
    public static void addToPool(KeyboardListener ks){
        k[counter] = ks;
        counter++;
    }
    
    public static void setHandler(IKeyboardHandler handler){
        KeyboardController.handler = handler;
    }
    
    public static boolean isKeyPressed(int key){
        return handler.isKeyDown(key);
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
