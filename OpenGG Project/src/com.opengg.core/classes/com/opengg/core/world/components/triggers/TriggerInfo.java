/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.triggers;

/**
 *
 * @author Javier
 */
public class TriggerInfo {
    public static final int SINGLE = 1;
    public static final int TOGGLE = 2;
    public Object data;
    public String info;
    public int type;
    public Object triggerSource;
    
}
