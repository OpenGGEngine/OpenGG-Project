/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.physics;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author Javier
 */
public class PhysicsObject {
    protected long id;

    protected PhysicsSystem system;
    protected PhysicsObject parent;
    protected List<PhysicsObject> children = new ArrayList<>();
    private Vector3f position = new Vector3f();
    private Quaternionf rotation = new Quaternionf();
    private Vector3f offset = new Vector3f();
    private Quaternionf rotoffset = new Quaternionf();
    private Vector3f scale = new Vector3f(1,1,1);

    private boolean serialize = true;


    public PhysicsObject(){
        id = UUID.randomUUID().getLeastSignificantBits();
    }

    public Vector3f getPosition() {
        return position;
    }

    private void recalculatePosition(){
        if(parent != null)
            position = parent.getPosition().add(parent.getRotation().transform(offset).multiply(parent.getScale()));
        else
            position = offset;

        children.forEach(PhysicsObject::recalculatePosition);
    }

    public PhysicsObject setPosition(Vector3f position) {
        this.offset = position; recalculatePosition();
        return this;
    }

    public Quaternionf getRotation(){
        return rotation;
    }

    private void recalculateRotation() {
        if(parent != null)
            rotation = rotoffset.multiply(parent.getRotation());
        else
            rotation = rotoffset;

        children.forEach(PhysicsObject::recalculatePosition);
        children.forEach(PhysicsObject::recalculateRotation);
    }

    public PhysicsObject setRotation(Quaternionf rotation) {
        this.rotoffset = rotation; recalculateRotation(); recalculatePosition();
        return this;
    }

    public Vector3f getScale() {
        if(parent != null) {
            return scale.multiply(parent.getScale());
        }else{
        }
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Vector3f getOffset() {
        return offset;
    }

    public Quaternionf getRotationOffset() {
        return rotoffset;
    }

    public PhysicsSystem getSystem(){
        return system;
    }

    public long getId(){
        return id;
    }

    public boolean shouldSerialize(){
        return serialize;
    }

    public void setSerialize(boolean serialize){
        this.serialize = serialize;
    }

    public void serialize(GGOutputStream out) throws IOException{}

    public void deserialize(GGInputStream in) throws IOException{}

    public void onSystemChange(){
        recalculatePosition();
        recalculateRotation();
    }

    public void setParent(PhysicsObject object){
        this.parent = object;
    }

    public void internalUpdate(float delta){
        this.update(delta);
        this.children.forEach(c -> c.internalUpdate(delta));
    }

    public void update(float delta){}

}
