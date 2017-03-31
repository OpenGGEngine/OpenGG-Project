/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.test;

import com.opengg.core.engine.WorldEngine;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.ModelLoader;
import com.opengg.core.world.components.ComponentHolder;
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.physics.PhysicsComponent;

/**
 *
 * @author Javier
 */
public class BulletComponent extends ComponentHolder{
    private GunComponent source;
    private ModelRenderComponent bullet;
    private PhysicsComponent physics;
    
    public BulletComponent(GunComponent source){
        this.source = source;
        this.setPositionOffset(source.getPosition().subtractThis(new Vector3f(0,-1,0)));
        this.setRotationOffset(source.getRotation());
        
        this.attach(new PhysicsComponent());
        bullet = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\45acp\\45acp.bmf"));
        attach(bullet);

        physics = new PhysicsComponent();
        physics.velocity = getRotationOffset().toEuler().multiplyThis(3);
        attach(physics);

        WorldEngine.getCurrent().addRenderable(bullet);
    }
    
    @Override
    public void update(float delta){
        if(this.getPosition().getDistance(source.getPosition()) > 500){
            parent.remove(this);
            WorldEngine.getCurrent().removeRenderable(bullet);
        }
    }
}
