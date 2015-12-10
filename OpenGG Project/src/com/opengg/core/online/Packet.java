/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.online;

import com.opengg.core.world.entities.resources.EntityFrame;
import com.opengg.core.world.physics.resources.PhysicsStruct;
import java.io.Serializable;

/**
 *
 * @author Javier
 */
public class Packet implements Serializable{
    PhysicsStruct c;// = new PhysicsStruct();
    EntityFrame[] e;
    public Packet(EntityFrame[] el){
        this.e = el;  
    }
    public Packet(PhysicsStruct c){
        this.c = c;
        
    }
    public Packet(EntityFrame[] el, PhysicsStruct c){
        this.e = el;
        this.c = c;
    }
    public Packet(){
        e = new EntityFrame[5];
        e[0] = new EntityFrame();
    }
}
