/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.math.Vector3f;
import com.opengg.core.world.Terrain;
import com.opengg.core.world.collision.BoundingBox;
import com.opengg.core.world.components.physics.CollisionComponent;
import com.opengg.core.world.collision.TerrainCollider;

/**
 *
 * @author Javier
 */
public class TerrainComponent extends RenderComponent{
    Terrain t;
    CollisionComponent c;
    
    public TerrainComponent(Terrain t){
        this.t = t;
    }
    
    public void enableRendering(){
        this.setDrawable(t.getDrawable());
        this.getWorld().addRenderable(this);
    }
    
    public void enableCollider(){
        c = new CollisionComponent(new BoundingBox(new Vector3f(-3000,-3000,-3000), 6000,6000,6000), new TerrainCollider(t));
        c.setForceTest(true);
        this.attach(c);
    }
}
