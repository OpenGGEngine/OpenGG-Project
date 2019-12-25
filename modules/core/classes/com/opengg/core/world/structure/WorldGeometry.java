/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.structure;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.RigidBody;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author Javier
 */
public abstract class WorldGeometry implements Renderable{
    private long guid;
    private Vector3f pos;
    private Quaternionf rot;
    private Vector3f scale;
    private Renderable renderable;
    private RigidBody collider;
    private WorldStructure parent;

    public WorldGeometry(){
        guid = UUID.randomUUID().getLeastSignificantBits();
    }

    public void initalize(Vector3f pos, Quaternionf rot, Vector3f scale){
        this.pos = pos;
        this.rot = rot;
        this.scale = scale;
    }

    public long getGuid() {
        return guid;
    }

    public Vector3f getPosition(){
        return pos;
    }

    public Quaternionf getRotation(){
        return rot;
    }

    public Vector3f getScale(){
        return scale;
    }

    public void setPosition(Vector3f pos) {
        this.pos = pos;
    }

    public void setRotation(Quaternionf rot) {
        this.rot = rot;
    }

    public void setRotation(Vector3f rot) {
        this.rot = Quaternionf.createYXZ(rot);
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Renderable getRenderable(){
        return renderable;
    }

    public RigidBody getCollider() {
        return collider;
    }

    public void setRenderable(Renderable renderable){
        this.renderable = renderable;
    }

    public void setCollider(RigidBody collider){
        this.collider = collider;
        this.collider.setSerialize(false);
    }

    public void setParent(WorldStructure parent){
        this.parent = parent;
        if(this.collider != null) {
            this.parent.getParent().getSystem().addObject(this.collider);
        }
    }

    public WorldStructure getParent() {
        return parent;
    }

    @Override
    public void render(){
        if(renderable != null){
            ShaderController.setPosRotScale(getPosition(), getRotation(), getScale());
            renderable.render();
        }
    }

    public final void localSerialize(GGOutputStream out) throws IOException{
        out.write(pos);
        out.write(rot);
        out.write(scale);
        out.write(renderable != null);
        out.write(collider != null);
        serialize(out);
    }

    public final void localDeserialize(GGInputStream in) throws IOException{
        pos = in.readVector3f();
        rot = in.readQuaternionf();
        scale = in.readVector3f();
        boolean draw = in.readBoolean();
        boolean collide = in.readBoolean();
        deserialize(in, draw, collide);
    }

    public final void deleteParts(){
        if(this.renderable != null)
            this.getParent().getParent().removeRenderable(this);
        if(this.collider != null)
            this.getParent().getParent().getSystem().removeObject(this.collider);
    }

    public final void delete(){
        deleteParts();
        this.getParent().removeGeometry(this);
    }

    public abstract void serialize(GGOutputStream out) throws IOException;
    public abstract void deserialize(GGInputStream in, boolean draw, boolean collide) throws IOException;

}
