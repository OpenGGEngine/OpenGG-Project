/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.entities;

import com.opengg.core.Model;
import com.opengg.core.Vector3f;
import com.opengg.core.util.Time;

/**
 *
 * @author ethachu19
 */
public abstract class EntityUpdate extends Entity {
    Time time = new Time();
    public float acceleration;
    public float lastAcceleration;
    public float velocity;
    public Vector3f direction = null;
    
    public EntityUpdate(Model m)
    {
        super(m); 
    }
}
