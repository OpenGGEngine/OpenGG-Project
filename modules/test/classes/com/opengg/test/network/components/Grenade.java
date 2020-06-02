package com.opengg.test.network.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.RigidBody;
import com.opengg.core.physics.collision.colliders.AABB;
import com.opengg.core.physics.collision.colliders.SphereCollider;
import com.opengg.core.render.objects.TextureRenderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.physics.RigidBodyComponent;

import java.awt.*;
import java.io.IOException;

public class Grenade extends Projectile{
    float timeSinceFire;
    private RenderComponent grenade;
    private RigidBodyComponent rigidBody;

    public Grenade(){
        super(3);

        rigidBody = new RigidBodyComponent(new RigidBody(new AABB(1,1,1), new SphereCollider(0.15f)), true);
        rigidBody.getRigidBody().getPhysicsProvider().get().mass = 0.1f;
        rigidBody.getRigidBody().restitution = 0.5f;
        rigidBody.getRigidBody().staticfriction = 0.5f;
        rigidBody.getRigidBody().dynamicfriction = 0.3f;

        attach(rigidBody);
    }

    public Grenade(Component source){
        this();
        this.setPositionOffset(source.getPosition().add(source.getRotation().transform(new Vector3f(0,0,0))));
        this.setRotationOffset(source.getRotation());
        rigidBody.getRigidBody().getPhysicsProvider().get().velocity = getRotation().transform(new Vector3f(0,0,10));
        setView(generateView());
    }

    @Override
    public void update(float delta){
        timeSinceFire += delta;
        if(timeSinceFire > 3.5f){
            OpenGG.asyncExec(() -> {
                /*var exp = new ExplosionParticleEmitter(10, 1f, Texture.ofColor(Color.ORANGE));
                exp.setPositionOffset(this.getPosition());
                WorldEngine.getCurrent().attach(exp);
                exp.fire(50);
                OpenGG.asyncExec(3, () -> {
                    //exp.delete();
                });*/
                this.delete();
            });
        }
    }

    @Override
    public void serializeUpdate(GGOutputStream out) throws IOException {
        super.serializeUpdate(out);
        out.write(timeSinceFire);
    }

    @Override
    public void deserializeUpdate(GGInputStream in, float delta) throws IOException {
        super.deserializeUpdate(in, delta);
        timeSinceFire = in.readFloat() + delta;

    }

    @Override
    public RenderComponent generateView() {
        return new RenderComponent(
                new TextureRenderable(
                        ObjectCreator.createQuadPrism(new Vector3f(-0.15f, -0.15f, -0.15f),
                                new Vector3f(0.15f, 0.15f, 0.15f)),
                        Texture.ofColor(Color.DARK_GRAY)));
    }
}
