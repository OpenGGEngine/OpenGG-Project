/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.structure;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Model;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.util.LambdaContainer;

import java.io.IOException;

/**
 *
 * @author Javier
 */
public abstract class WorldGeometry {
    private Vector3f pos;
    private Quaternionf rot;
    private Vector3f scale;
    private Drawable drawable;
    private ColliderGroup collider;

    public WorldGeometry(){}

    public void initalize(Vector3f pos, Quaternionf rot, Vector3f scale){
        this.pos = pos;
        this.rot = rot;
        this.scale = scale;
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

    public Drawable getDrawable(){
        return drawable;
    }

    public void setDrawable(Drawable drawable){
        if(this.drawable == null)
            this.drawable = drawable;
        else
            throw new UnsupportedOperationException("Tried to set drawable on WorldGeometry that already had one");
    }

    public void setCollider(ColliderGroup collider){
        if(this.collider == null)
            this.collider = collider;
        else
            throw new UnsupportedOperationException("Tried to set collider on WorldGeometry that already had one");
    }

    public void render(){
        if(drawable != null){
            var matrix = new Matrix4f().scale(getScale()).translate(getPosition()).rotate(getRotation());
            drawable.setMatrix(matrix);
            drawable.render();
        }
    }

    public final void localSerialize(GGOutputStream out) throws IOException{
        out.write(pos);
        out.write(rot);
        out.write(scale);
        out.write(drawable != null);
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

    public abstract void serialize(GGOutputStream out) throws IOException;
    public abstract void deserialize(GGInputStream in, boolean draw, boolean collide) throws IOException;

}
