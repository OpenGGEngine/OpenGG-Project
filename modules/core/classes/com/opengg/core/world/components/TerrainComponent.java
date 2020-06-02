/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.RigidBody;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.Terrain;
import com.opengg.core.physics.collision.colliders.AABB;
import com.opengg.core.physics.collision.colliders.TerrainCollider;
import com.opengg.core.world.components.physics.RigidBodyComponent;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Javier
 */
public class TerrainComponent extends RenderComponent{
    Terrain terrain;
    boolean collideable;
    private Texture blotmap = null;//Texture.get2DTexture(TextureManager.getDefault());
    private Texture array = null;//Texture.getArrayTexture(TextureManager.getDefault(),
            //TextureManager.getDefault(),TextureManager.getDefault(),TextureManager.getDefault());
    
    public TerrainComponent(){
        this.setShader("terrain");
    }
    
    public TerrainComponent(Terrain terrain){
        this();
        this.terrain = terrain;
        this.setRenderable(terrain.getRenderable());
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

                this.attach(new RigidBodyComponent(new RigidBody(aabb, mesh), false));

                collideable = true;
            }
        }
    }

    public void setBlotmap(TextureData data){
        this.blotmap = Texture.create(Texture.config(), data);
    }

    public Texture getBlotmap(){
        return this.blotmap;
    }

    public void setGroundArray(List<TextureData> tex){
        this.array = Texture.create(Texture.arrayConfig(), tex.subList(0,4));
    }


    public void setIndividualGroundArrayValue(TextureData tex, int index){
        var list = IntStream.range(0,4)
                .mapToObj(i -> i == index ? tex : this.getGroundArray().getData().get(i))
                .collect(Collectors.toList());
        setGroundArray(list);
    }

    public Texture getGroundArray(){
        return this.array;
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
        if(blotmap == null || array == null)
        ShaderController.setUniform("Ka", blotmap);
        ShaderController.setUniform("terrain", array);
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
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);

        String terrainPath = in.readString();
        terrain = Terrain.generate(terrainPath);

        String blot = in.readString();
        String s1 = in.readString(), s2 = in.readString(), s3 = in.readString(), s4 = in.readString();
        OpenGG.asyncExec(() -> {
            this.setRenderable(terrain.getRenderable());
            setBlotmap(Resource.getTextureData(blot));
            setGroundArray(List.of(s1,s2,s3,s4).stream().map(Resource::getTextureData).collect(Collectors.toList()));
        });
    }
}
