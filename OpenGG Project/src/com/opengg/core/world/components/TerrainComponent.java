/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.Terrain;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.world.components.physics.CollisionComponent;
import com.opengg.core.physics.collision.TerrainCollider;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class TerrainComponent extends RenderComponent{
    Terrain terrain;
    CollisionComponent c;
    public Texture blotmap = Texture.get2DTexture(TextureManager.getDefault());
    public Texture array;
    
    public TerrainComponent(){}
    
    public TerrainComponent(Terrain terrain){
        this.terrain = terrain;
        this.shader = "terrain";
        this.setDrawable(terrain.getDrawable());
    }
    
    public void enableCollider(){
        c = new CollisionComponent(new AABB(new Vector3f(-15000,-15000,-15000), 15000,15000,15000), new TerrainCollider(terrain));
        this.attach(c);
    }
    
    public void setGroundArray(Texture tex){
        this.array = tex;
    }
    
    public void setBlotmap(Texture tex){
        this.blotmap = tex;
    }
    
    public Terrain getTerrain(){
        return terrain;
    }
    
    public float getHeightAt(Vector3f pos){
        Vector3f np = pos.subtract(getPosition()).divide(getScale());
        float height = terrain.getHeight(np.x, np.z);
        return (height + getPosition().y) * getScale().y;  
    }
    
    public Vector3f getNormalAt(Vector3f pos){
        Vector3f np = pos.subtract(getPosition()).divide(getScale());
        Vector3f normal = terrain.getNormalAt(np.x, np.z);
        return normal;
    }
    
    @Override
    public void render(){
        this.blotmap.use(1);
        this.array.use(0);
        super.render();
    }
    
    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(terrain.getSource());
        out.write(blotmap.getData().get(0).source);
        out.write(array.getData().get(0).source);
        out.write(array.getData().get(1).source);
        out.write(array.getData().get(2).source);
        out.write(array.getData().get(3).source);
        out.write(c != null);
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        terrain = Terrain.generate(in.readString());
        blotmap = Texture.get2DTexture(in.readString());
        array = Texture.getArrayTexture(in.readString(), in.readString(), in.readString(), in.readString());
        if(in.readBoolean()){
            enableCollider();
        }
    }
}
