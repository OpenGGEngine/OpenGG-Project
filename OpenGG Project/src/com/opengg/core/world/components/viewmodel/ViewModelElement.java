/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

/**
 *
 * @author Javier
 */
public class ViewModelElement {
    public static final int INTEGER = 0, 
            FLOAT = 1, 
            STRING = 2, 
            COMPONENT = 3, 
            VECTOR3F = 4, 
            VECTOR2F = 5, 
            VECTOR4F = 6, 
            QUATERNIONF = 7,
            BOOLEAN = 8;
    
    public String name;
    public String internalname;
    public boolean autoupdate;
    public int type;
    public Object value;
    public boolean visible;
    
    ComponentViewModel cmv;
}
