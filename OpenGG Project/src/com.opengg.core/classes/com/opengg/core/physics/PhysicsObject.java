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
import java.util.stream.Collectors;

/**
 *
 * @author Javier
 */
public class PhysicsObject {
    public static int idcount = 0;

    public int id;

    public PhysicsSystem system;
    public PhysicsObject parent;
    protected List<PhysicsObject> children = new ArrayList<>();
    private Vector3f position = new Vector3f();
    private Quaternionf rotation = new Quaternionf();
    private Vector3f offset = new Vector3f();
    private Quaternionf rotoffset = new Quaternionf();
    private Vector3f scale = new Vector3f(1,1,1);

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

    public void setPosition(Vector3f position) {
        this.offset = position; recalculatePosition();
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

    public void setRotation(Quaternionf rotation) {
        this.rotoffset = rotation; recalculateRotation(); recalculatePosition();
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

    public int getId(){
        return id;
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
