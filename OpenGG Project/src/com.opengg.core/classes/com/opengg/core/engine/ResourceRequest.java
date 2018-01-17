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
public class ResourceRequest {
    public static final int TEXTURE=0, MODEL=1, WORLD=2, SOUND=3;
    
    public String location;
    public int type;
    
    public ResourceRequest(String location, int type){
        this.location = location;
        this.type = type;
    }
}
