/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.structure;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;

/**
 *
 * @author Javier
 */
public abstract class WorldGeometry {
    private Vector3f pos = new Vector3f();
    private Quaternionf rot = new Quaternionf();
    private Vector3f scale = new Vector3f();
    
    public void render(){
        
    }
}
