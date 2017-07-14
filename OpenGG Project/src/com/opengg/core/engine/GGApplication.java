/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

/**
 *
 * @author Javier
 */
public abstract class GGApplication{
    public String applicationName;
    public long applicationID;
    public abstract void setup();
    public abstract void render();
    public abstract void update(float delta);
}
