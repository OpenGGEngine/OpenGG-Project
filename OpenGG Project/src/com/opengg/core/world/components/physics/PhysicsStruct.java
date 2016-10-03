/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.physics;

/**
 *
 * @author Javier
 */
public class PhysicsStruct {
    byte[] structInfo = new byte[25*4];
    public PhysicsStruct(PhysicsComponent p){
        int i = 0;
        for(byte b : p.pos.toByteArray()){
            structInfo[i] = b;
            i++;
        }
        for(byte b : p.velocity.toByteArray()){
            structInfo[i] = b;
            i++;
        }
        for(byte b : p.rotVelocity.toByteArray()){
            structInfo[i] = b;
            i++;
        }
        for(byte b : p.force.toByteArray()){
            structInfo[i] = b;
            i++;
        }
        for(byte b : p.rotForce.toByteArray()){
            structInfo[i] = b;
            i++;
        }
        for(byte b : p.acceleration.toByteArray()){
            structInfo[i] = b;
            i++;
        }
        for(byte b : p.rotAcceleration.toByteArray()){
            structInfo[i] = b;
            i++;
        }
    }
    public byte[] getByteArray(){
        return structInfo;
    }
}
