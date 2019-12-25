package com.opengg.test.network.components;

import com.opengg.core.GGInfo;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.network.common.ChatMessage;
import com.opengg.core.physics.RigidBody;
import com.opengg.core.physics.collision.colliders.AABB;
import com.opengg.core.physics.collision.colliders.ConvexHull;
import com.opengg.core.render.drawn.TextureRenderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.WorldObject;
import com.opengg.core.world.components.physics.RigidBodyComponent;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Character extends Component{
    int team;

    protected RigidBodyComponent phys;
    protected WorldObject head;
    protected WorldObject arm1;
    protected WorldObject arm2;
    protected Component inventory;

    Vector3f movementDir = new Vector3f();
    Vector3f rotationDir = new Vector3f();
    boolean jump = false;
    float acceleration = 20;
    float targetSpeed = 5;
    float headRange = 20;

    float health = 100;

    public Character(){
        var bodyCube = List.of(
                new Vector3f(-0.3f,0,-0.1f),
                new Vector3f(-0.3f,0,0.1f),
                new Vector3f(0.3f,0,-0.1f),
                new Vector3f(0.3f,0,0.1f),
                new Vector3f(-0.3f,1.8f,-0.1f),
                new Vector3f(-0.3f,1.8f,0.1f),
                new Vector3f(0.3f,1.8f,-0.1f),
                new Vector3f(0.3f,1.8f,0.1f)
        );
        var bodyCollider = new ConvexHull(bodyCube);

        var headCube = List.of(
                new Vector3f(-0.15f,0,-0.15f),
                new Vector3f(-0.15f,0,0.15f),
                new Vector3f(0.15f,0,-0.15f),
                new Vector3f(0.15f,0,0.15f),
                new Vector3f(-0.15f,0.3f,-0.15f),
                new Vector3f(-0.15f,0.3f,0.15f),
                new Vector3f(0.15f,0.3f,-0.15f),
                new Vector3f(0.15f,0.3f,0.15f)
        );
        var headCollider = new ConvexHull(headCube);
        headCollider.setPosition(new Vector3f(0,1.8f,0));

        var wholeCollider = new RigidBody(
                new AABB(2,4,2), bodyCollider, headCollider);

        wholeCollider.addListener((s,i)-> {
            var body = ((RigidBody)i.data);
            if(body.boundToComponent()){
                var parent = body.getComponentOwner().getParent();
                if(parent instanceof Projectile){
                    this.damage(((Projectile)parent).damage);
                }
            }
        });

        head = new WorldObject();
        head.setPositionOffset(new Vector3f(0,1.8f,0));
        head.setAbsoluteRotation(true);
        head.setName("head");
        attach(head);

        arm1 = new WorldObject();
        arm1.setAbsoluteRotation(true);
        arm1.setPositionOffset(new Vector3f(0.33f,1.78f,0));
        arm1.setName("arm1");
        attach(arm1);

        arm2 = new WorldObject();
        arm2.setAbsoluteRotation(true);
        arm2.setPositionOffset(new Vector3f(-0.33f,1.78f,0));
        arm2.setName("arm2");
        attach(arm2);

        phys = new RigidBodyComponent(wholeCollider, true);
        phys.getRigidBody().getPhysicsProvider().get().mass = 100f;
        phys.getRigidBody().getPhysicsProvider().get().applyRotationChangeOnCollision = false;
        phys.getRigidBody().restitution = 0;
        phys.setName("physics");
        attach(phys);


        attach(inventory = new WorldObject().setName("inventory"));
        this.findByName("inventory").get(0).setEnabled(false);
    }

    private void generateRenderables(){
        head.attach(new RenderComponent(new TextureRenderable(
                ObjectCreator.createQuadPrism(new Vector3f(-0.15f, 0, -0.15f), new Vector3f(0.15f, 0.3f, 0.15f)),
                Texture.ofColor(Color.GRAY))).setSerializable(false));
        arm1.attach(new RenderComponent(new TextureRenderable(
                ObjectCreator.createQuadPrism(new Vector3f(-0.08f, -0.08f, 0), new Vector3f(0.08f, 0.08f, 0.6f)),
                Texture.ofColor(Color.GRAY))).setSerializable(false));
        arm2.attach(new RenderComponent(new TextureRenderable(
                ObjectCreator.createQuadPrism(new Vector3f(-0.08f, -0.08f, 0), new Vector3f(0.08f, 0.08f, 0.6f)),
                Texture.ofColor(Color.GRAY))).setSerializable(false));
        this.attach(new RenderComponent(new TextureRenderable(
                ObjectCreator.createQuadPrism(new Vector3f(-0.3f, 0, -0.15f), new Vector3f(0.3f, 1.8f, 0.15f)),
                Texture.ofColor(Color.GRAY))).setSerializable(false));
    }

    @Override
    public void update(float delta){
        var movement = getRotation().transform(this.movementDir.multiply(acceleration));
        head.setRotationOffset(Quaternionf.createYXZ(rotationDir));

        var diff = head.getRotation().toEuler().y - getRotation().toEuler().y;

        var provider = phys.getRigidBody().getPhysicsProvider().get();
        if(movement.lengthSquared() != 0){
            var newSpeed = provider.velocity.add(movement.multiply(delta));
            provider.velocity = newSpeed.length() > targetSpeed ? newSpeed.normalize().multiply(targetSpeed) : newSpeed;
        }else{
            var newSpeed = provider.velocity.subtract(provider.velocity.setY(0).multiply(delta * acceleration));
            provider.velocity = newSpeed;
        }

        if(jump) {
            provider.velocity = provider.velocity.add(new Vector3f(0, 5, 0));
            jump = false;
        }

        if(provider.velocity.xz().length() < 0.01f){
            provider.velocity = provider.velocity.setX(0).setZ(0);
            if(diff > headRange){
                setRotationOffset(getRotation().multiply(Quaternionf.createYXZ(new Vector3f(0, diff-headRange, 0))));
            }else if(diff < -headRange){
                setRotationOffset(getRotation().multiply(Quaternionf.createYXZ(new Vector3f(0, diff+headRange, 0))));
            }
        }else{

        }setRotationOffset(Quaternionf.createYXZ(rotationDir.setX(0).setZ(0)));

        if(getCurrentItem().isPresent()){
            arm1.setRotationOffset(Quaternionf.createYXZ(new Vector3f(rotationDir.x*-1, rotationDir.y-180,0)));
            arm2.setRotationOffset(Quaternionf.createYXZ(new Vector3f(88,rotationDir.y-180,0)));
        }else {
            arm1.setRotationOffset(Quaternionf.createYXZ(new Vector3f(88,rotationDir.y-180,0)));
            arm2.setRotationOffset(Quaternionf.createYXZ(new Vector3f(88,rotationDir.y-180,0)));
        }
    }

    public void damage(float damage){
        health -= damage;
        if(health <= 0){
            kill();
        }
    }

    public void kill(){
        if(!GGInfo.isServer()) return;

        NetworkEngine.getServer().getClients().forEach(c -> new ChatMessage("", this.getName() + " has died").send(c.getConnection()));
        onDeath();
    }

    public void onDeath(){

    }


    public List<Item> getItems(){
        return inventory.getChildren().stream().map(c -> (Item)c).collect(Collectors.toList());
    }

    public void useItem(Item item){
        var itemOpt = getCurrentItem();

        if (itemOpt.isPresent()) {
            var currentItem = itemOpt.get();
            inventory.attach(currentItem);
            currentItem.setEnabled(false);
        }

        arm1.attach(item);
        item.setPositionOffset(new Vector3f(0,0,0.59f));
        item.setEnabled(true);
    }

    public void removeItem(boolean drop){
        var itemOpt = getCurrentItem();
        if(itemOpt.isEmpty()) return;
        var item = itemOpt.get();
        if(drop){
            var container = new WorldItemContainer(item);
            WorldEngine.getCurrent().attach(container);
            container.setPositionOffset(this.getPositionOffset().add(this.getRotation().transform(new Vector3f(0,1.5f,-1f))));
        }else {
            item.delete();
        }

        if(!inventory.getChildren().isEmpty()){
            useItem((Item) inventory.getChildren().get(0));
        }
    }

    public Optional<Item> getCurrentItem(){
        return arm1.hasChildWithName("item") ? Optional.of((Item)arm1.findByName("item").get(0)) : Optional.empty();
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException {
        super.deserialize(in);
        OpenGG.asyncExec(() -> {
            head = (WorldObject) findByName("head").get(0);
            arm1 = (WorldObject) findByName("arm1").get(0);
            arm2 = (WorldObject) findByName("arm2").get(0);
            phys = (RigidBodyComponent) findByName("physics").get(0);
            inventory = findByName("inventory").get(0);
            generateRenderables();
        });
    }


    @Override
    public void serializeUpdate(GGOutputStream out) throws IOException {
        super.serializeUpdate(out);
        out.write(health);
        out.write(this.movementDir);
        out.write(this.rotationDir);
    }

    @Override
    public void deserializeUpdate(GGInputStream in, float delta) throws IOException {
        super.deserializeUpdate(in, delta);
        health = in.readFloat();
        movementDir = in.readVector3f();
        rotationDir = in.readVector3f();
    }
}
