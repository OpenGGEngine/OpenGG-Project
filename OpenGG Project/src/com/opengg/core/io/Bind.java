/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.io;

/**
 *
 * @author Javier
 */
public class Bind {
    public ControlType type;
    public String action;
    public int button;
    
    public Bind(ControlType type, String action, int button){
        this.type = type;
        this.action = action;
        this.button = button;
    }
}
