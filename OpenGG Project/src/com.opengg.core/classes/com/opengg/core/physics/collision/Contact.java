/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.physics.collision;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class Contact {
    List<ContactManifold> manifolds = new ArrayList<>();
    
    public Contact(ContactManifold manifold){
        manifolds.add(manifold);
    }
    
    public Contact(List<ContactManifold> manifold){
        manifolds.addAll(manifold);
    }
    
    public Contact(){
        
    }
    
    public Contact reverse(){
        for(ContactManifold manifold : manifolds){
            manifold.reverse();
        }
        return this;
    }
}








