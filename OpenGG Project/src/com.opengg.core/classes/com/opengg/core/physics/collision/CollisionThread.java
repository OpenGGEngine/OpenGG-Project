/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision;

/**
 *
 * @author Javier
 */
public class CollisionThread implements Runnable{
    ColliderGroup g1, g2;
    Collision c;
    public CollisionThread(ColliderGroup g1, ColliderGroup g2){
        this.g1 = g1;
        this.g2 = g2;
    }

    @Override
    public void run() {
        c = g1.testForCollision(g2);
    }
}
