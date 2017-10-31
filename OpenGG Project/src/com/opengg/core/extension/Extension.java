/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.extension;

/**
 *
 * @author Javier
 */
public abstract class Extension {
    public static final int NONE = 0, LWJGL = 1, CONFIG = 2, GRAPHICS = 3;
    
    public String extname = "DEFAULT";
    public int requirement = GRAPHICS;
    
    public abstract void loadExtension();
    
    public void update(){}
    
    public void render(){}
}
