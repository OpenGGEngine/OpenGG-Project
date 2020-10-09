/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.test.network.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.physics.RigidBody;
import com.opengg.core.physics.collision.colliders.SphereCollider;
import com.opengg.core.render.objects.TextureRenderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.colliders.AABB;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.physics.RigidBodyComponent;

import java.awt.*;

/**
 *
 * @author Javier
 */
public class Bullet extends Projectile{
    float timeSinceFire;
    private RenderComponent bullet;
    private RigidBodyComponent rigidBody;

    public Bullet(){
        super(20);

        rigidBody = new RigidBodyComponent(new RigidBody(new AABB(1,1,1), new SphereCollider(0.01f)), true);
        rigidBody.getRigidBody().getPhysicsProvider().get().mass = 0.05f;
        rigidBody.getRigidBody().getPhysicsProvider().get().applyGravity = false;
        rigidBody.getRigidBody().restitution = 0.9f;
        rigidBody.getRigidBody().dynamicfriction = 0;
        attach(rigidBody);
    }

    public Bullet(Gun source){
        this();
        this.setPositionOffset(source.getPosition().add(source.getRotation().transform(new Vector3f(0,0,0))));
        this.setRotationOffset(source.getRotation());

        rigidBody.getRigidBody().getPhysicsProvider().get().velocity = getRotation().transform(new Vector3f(0,0,10));

        setView(generateView());
    }
    
    @Override
    public void update(float delta){
        timeSinceFire += delta;
        if(timeSinceFire > 1f){
            OpenGG.asyncExec(this::delete);
        }
    }

    @Override
    public RenderComponent generateView() {
        return bullet = new RenderComponent(
                new TextureRenderable(ObjectCreator.createQuadPrism(new Vector3f(-0.015f,-0.015f,-0.015f), new Vector3f(0.015f,0.015f,0.03f)),
                        Texture.ofColor(Color.ORANGE)));
    }
}
