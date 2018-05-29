/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

/**
 *
 * @author Javier
 */
public class Element {
    public String name = "Default";
    public String internalname;
    public boolean autoupdate = true;
    public Type type;
    public Object value;
    public boolean visible = true;
    public boolean forceupdate = false;

    public Element name(String name){
        this.name = name;
        return this;
    }

    public Element internalName(String internalname){
        this.internalname = internalname;
        return this;
    }

    public Element autoUpdate(boolean autoupdate){
        this.autoupdate = autoupdate;
        return this;
    }

    public Element type(Type type){
        this.type = type;
        return this;
    }

    public Element value(Object value){
        this.value = value;
        return this;
    }

    public Element visible(boolean visible){
        this.visible = visible;
        return this;
    }

    public Element forceUpdate(boolean forceupdate){
        this.forceupdate = forceupdate;
        return this;
    }

    public enum Type{
        INTEGER,
        FLOAT,
        STRING,
        COMPONENT,
        VECTOR3F,
        VECTOR2F,
        VECTOR4F,
        QUATERNIONF,
        BOOLEAN,
        TEXTURE,
        MODEL
    }
}
