/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.extension;

/**
 * //TODO
 * @author Javier
 */
public abstract class Extension {
    public static final int NONE = 0, LWJGL = 1, CONFIG = 2, GRAPHICS = 3;
    
    public String extname = "DEFAULT";
    public int requirement = GRAPHICS;
    public boolean initialized = false;

    /**
     * Called on engine initialization
     */
    public abstract void loadExtension();

    /**
     * Called once per update cycle once initialized
     * @param delta delta time (in seconds) since initialization
     */
    public void update(float delta){}

    /**
     * Called once per frame once initialized
     */
    public void render(){}
}
