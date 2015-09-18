/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.entities;

import com.opengg.core.Model;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 19coindreauj
 */
public abstract class EntityFactory {
    static public int entityCount = 0;
    List<Entity> list = new ArrayList<>();
    public boolean generatePhysicsEntity(PhysicsEntity p, Model m){
        entityCount++;
        p = new PhysicsEntity(m);
        return list.add(p);
    }
    
}
