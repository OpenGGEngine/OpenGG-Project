/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.Terrain;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.TerrainCollider;
import com.opengg.core.world.components.physics.CollisionComponent;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class TerrainComponent extends RenderComponent{
    Terrain terrain;
    boolean collideable;
    private Texture blotmap = Texture.get2DTexture(TextureManager.getDefault());
    private Texture array = Texture.getArrayTexture(TextureManager.getDefault(),
            TextureManager.getDefault(),TextureManager.getDefault(),TextureManager.getDefault());
    
    public TerrainComponent(){}
    
    public TerrainComponent(Terrain terrain){
        this.terrain = terrain;
        this.setShader("terrain");
        this.setDrawable(terrain.getDrawable());
    }

    public void enableCollider(){
        final int sectionsize = 8;
        float[][] map = terrain.getMap();
        int length = map.length;
        int width = map[0].length;
        int divx = length/sectionsize;
        
        int divz = width/sectionsize;
        
        for(int i = 0; i < divx; i++){
            for(int j = 0; j < divz; j++){
                TerrainCollider mesh = new TerrainCollider(terrain,terrain.getMesh(i*sectionsize, j*sectionsize, sectionsize, sectionsize));

                AABB aabb = new AABB(1f/divx/2,10,1f/divz/2f);
                aabb.setPosition(new Vector3f((1f/divx)*i+(1f/divx*0.5f),0, (1f/divz)*j+(1f/divz*0.5f)));

                this.attach(new CollisionComponent(new ColliderGroup(aabb, mesh)));

                collideable = true;
            }
        }
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
        return (height) * getScale().y + getPosition().y;  
    }
    
    public Vector3f getNormalAt(Vector3f pos){
        Vector3f np = pos.subtract(getPosition()).divide(getScale());
        return terrain.getNormalAt(np.x, np.z);
    }

    @Override
    public void render(){
        blotmap.use(1);
        array.use(11);
        ShaderController.setUniform("scale", this.getScale());
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
        out.write(collideable);
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        String tp = in.readString();
        terrain = Terrain.generate(tp);

        String blot = in.readString();

        String s1 = in.readString(), s2 = in.readString(), s3 = in.readString(), s4 = in.readString();


        setBlotmap(Resource.getTexture(blot));
        setGroundArray(Texture.create(Texture.arraySRGBConfig(), Resource.getTextureData(s1), Resource.getTextureData(s2), Resource.getTextureData(s3), Resource.getTextureData(s4)));
        if(in.readBoolean()){
            enableCollider();
        }
    }
}
