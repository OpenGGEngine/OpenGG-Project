/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics;

/**
 *
 * @author Javier
 */
public class PhysicsEngine {
    static boolean enablePhysics = true;
    static PhysicsSystem instance;
    
    public static void initialize(){

    }

    public static void updatePhysics(float delta){
        if(enablePhysics)
            instance.update(delta);
    }

    public static void setInstance(PhysicsSystem physics) {
        instance = physics;
    }

    public static boolean isEnabled() {
        return enablePhysics;
    }

    public static void setEnabled(boolean enablePhysics) {
        PhysicsEngine.enablePhysics = enablePhysics;
    }
    
    public static PhysicsSystem getInstance(){
        return instance;
    }

    private PhysicsEngine() {
    }
}
