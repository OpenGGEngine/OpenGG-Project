/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io.input.keyboard;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class KeyboardController {
    static IKeyboardHandler handler;
    static List<KeyboardListener> k = new ArrayList<>();
    static List<KeyboardCharacterListener> kc = new ArrayList<>();

    public static int charToKeycode(char ch){
        if(Character.isAlphabetic(ch))
            return Character.toUpperCase(ch);
        else return ch;
            
    }
    
    public static char keycodeToChar(int key){
        return (char)key;
    }
    
    public static void addKeyboardListener(KeyboardListener ks){
        k.add(ks);
    }

    public static void addKeyboardCharacterListener(KeyboardCharacterListener ks){
        kc.add(ks);
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

            if(key >= 32 && key < 127){
                char c = keycodeToChar(key);
                c = Character.toLowerCase(c);
                if(handler.isKeyDown(Key.KEY_LEFT_SHIFT) || handler.isKeyDown(Key.KEY_RIGHT_SHIFT))
                    if(Character.isAlphabetic(c))
                        c = Character.toUpperCase(c);

                for(KeyboardCharacterListener kc1: kc){
                    kc1.charPressed(c);
                }
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

    private KeyboardController() {
    }
    
}
