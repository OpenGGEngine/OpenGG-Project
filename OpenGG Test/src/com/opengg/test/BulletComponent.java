/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.test;

import com.opengg.core.engine.RenderEngine;
import com.opengg.core.engine.Resource;
import com.opengg.core.engine.WorldEngine;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.light.Light;
import com.opengg.core.world.collision.AABB;
import com.opengg.core.world.collision.CylinderCollider;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.LightComponent;
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.physics.CollisionComponent;
import com.opengg.core.world.components.physics.PhysicsComponent;

/**
 *
 * @author Javier
 */
public class BulletComponent extends Component{
    float timeSinceFire;
    private GunComponent source;
    private ModelRenderComponent bullet;
    private PhysicsComponent physics;
    private LightComponent lc;
    
    public BulletComponent(GunComponent source){
        this.source = source;
        this.setPositionOffset(source.getPosition().add(source.getRotation().transform(new Vector3f(0,0.5f,0))));
        this.setRotationOffset(source.getRotation());
        
        this.attach(new PhysicsComponent());
        bullet = new ModelRenderComponent(Resource.getModel("45acp"));
        bullet.setRotationOffset(new Quaternionf(new Vector3f(0,0,-90)));
        bullet.setScale(new Vector3f(0.3f,0.3f,0.3f));
        attach(bullet);

        physics = new PhysicsComponent();
        physics.velocity = getRotation().transform(new Vector3f(5,0,0));
        physics.addCollider(new CollisionComponent(new AABB(new Vector3f(-1,-1,-1),1,1,1), new CylinderCollider(0.1f,0.1f)));
        physics.bounciness = 0.9f;
        physics.frictionCoefficient = 0;
        attach(physics);

        lc = new LightComponent(new Light(new Vector3f(), new Vector3f(1,0.3f,0.3f),200,300));
        attach(lc);
        
        WorldEngine.getCurrent().addRenderable(bullet);
        WorldEngine.getCurrent().addCollider(physics.getColliders().get(0));
    }
    
    @Override
    public void update(float delta){
        timeSinceFire += delta;
        if(timeSinceFire > 1f){
            RenderEngine.removeLight(lc.getLight());
        }
        
        if(this.getPosition().getDistance(source.getPosition()) > 500){
            RenderEngine.removeLight(lc.getLight());
            WorldEngine.markForRemoval(this);
            WorldEngine.getCurrent().removeRenderable(bullet);
        }
    }
}
