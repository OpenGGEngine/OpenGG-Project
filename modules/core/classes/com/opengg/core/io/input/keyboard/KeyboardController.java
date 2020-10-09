/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io.input.keyboard;

import com.opengg.core.console.GGConsole;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
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
    static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static int charToKeycode(char ch){
        if(Character.isAlphabetic(ch))
            return Character.toUpperCase(ch);
        else return ch;
            
    }
    
    public static char keycodeToChar(int key){
        return (char)key;
    }

    public static void clearKeyboardListeners(){
        k.clear();
        kc.clear();
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
                if(handler.isKeyDown(Key.KEY_LEFT_CONTROL)){
                    if(c == 'v'){
                        try {
                            var data = clipboard.getData(DataFlavor.stringFlavor);
                            var sdata = (String) data;
                            for(var chara : sdata.toCharArray()){
                                for(KeyboardCharacterListener kc1: kc){
                                    kc1.charPressed(chara);
                                }
                            }
                        } catch (UnsupportedFlavorException e) {
                            GGConsole.warning("Unsupported clipboard operation");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return;
                    }
                }
                if(handler.isKeyDown(Key.KEY_LEFT_SHIFT) || handler.isKeyDown(Key.KEY_RIGHT_SHIFT)) {
                    if (Character.isAlphabetic(c)) {
                        c = Character.toTitleCase(c);
                    }
                }

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
