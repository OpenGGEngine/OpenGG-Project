/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.ArrayTexture;
import com.opengg.core.render.texture.Texture;
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
    public Texture blotmap = Texture.blank;
    public ArrayTexture wow;
    
    public TerrainComponent(Terrain t){
        this.t = t;
        this.shader = "terrain";
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
    
    public void setGroundArray(ArrayTexture tex){
        this.wow = tex;
    }
    
    public void setBlotmap(Texture tex){
        this.blotmap = tex;
    }
    
    public Terrain getTerrain(){
        return t;
    }
    
    @Override
    public void render(){
        this.blotmap.useTexture(1);
        this.wow.useTexture(0);
        super.render();
    }
}
