/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.editor;

import com.opengg.core.exceptions.DataBindingException;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector4f;
import com.opengg.core.model.Model;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.world.components.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.opengg.core.editor.DataBinding.Type.*;

/**
 * Represents a data binding between an engine object and its representation in a visual editor
 * @author Javier
 */
public abstract class DataBinding<T> {
    public String name = "Default";
    public String internalname;

    public boolean autoupdate = true;
    public boolean visible = true;
    public boolean forceupdate = false;

    public final Type type;
    private T value;

    private Consumer<T> onUpdate = (t) -> {};
    private Consumer<T> onViewChange = (t) -> {};
    private Supplier<T> valueGetter = () -> {throw new DataBindingException("No value accessor supplied");};

    private DataBinding(Type type){
        this.type = type;
    }

    /**
     * Retusn a DataBinding generified to the type represented by the Type passed
     * @param type
     * @return
     */
    public static DataBinding ofType(Type type){
        return switch (type){
            case INTEGER -> new IntBinding();
            case FLOAT -> new FloatBinding();
            case STRING -> new StringBinding();
            case COMPONENT -> new ComponentBinding();
            case VECTOR3F -> new Vector3fBinding();
            case VECTOR2F -> new Vector2fBinding();
            case VECTOR4F -> new Vector4fBinding();
            case QUATERNIONF -> new QuaternionfBinding();
            case BOOLEAN -> new BooleanBinding();
            case TEXTURE -> new TextureBinding();
            case MODEL -> new ModelBinding();
        };
    }

    public static class StringBinding extends DataBinding<String>{
        public StringBinding(){
            super(STRING);
        }
    }

    public static class FloatBinding extends DataBinding<Float>{
        public FloatBinding(){
            super(FLOAT);
        }
    }

    public static class IntBinding extends DataBinding<Integer>{
        public IntBinding(){
            super(INTEGER);
        }
    }

    public static class BooleanBinding extends DataBinding<Boolean>{
        public BooleanBinding(){
            super(BOOLEAN);
        }
    }

    public static class TextureBinding extends DataBinding<TextureData>{
        public TextureBinding(){
            super(TEXTURE);
        }
    }

    public static class ModelBinding extends DataBinding<Model>{
        public ModelBinding(){
            super(MODEL);
        }
    }

    public static class ComponentBinding extends DataBinding<Component>{
        public ComponentBinding(){
            super(COMPONENT);
        }
    }

    public static class Vector2fBinding extends DataBinding<Vector2f>{
        public Vector2fBinding(){
            super(VECTOR2F);
        }
    }

    public static class Vector3fBinding extends DataBinding<Vector3f>{
        public Vector3fBinding(){
            super(VECTOR3F);
        }
    }

    public static class Vector4fBinding extends DataBinding<Vector4f>{
        public Vector4fBinding(){
            super(VECTOR4F);
        }
    }

    public static class QuaternionfBinding extends DataBinding<Quaternionf>{
        public QuaternionfBinding(){
            super(QUATERNIONF);
        }
    }

    /**
     * Sets the UI visible name of this DataBinding
     * @param name
     * @return
     */
    public DataBinding name(String name){
        this.name = name;
        return this;
    }

    /**
     * Sets the internal name of this DataBinding
     * @param name
     * @return
     */
    public DataBinding internalName(String internalname){
        this.internalname = internalname;
        return this;
    }

    public DataBinding autoUpdate(boolean autoupdate){
        this.autoupdate = autoupdate;
        return this;
    }

    public DataBinding visible(boolean visible){
        this.visible = visible;
        return this;
    }

    public DataBinding forceUpdate(boolean forceupdate){
        this.forceupdate = forceupdate;
        return this;
    }

    public DataBinding onDataChange(Consumer<T> onChange){
        this.onUpdate = onChange;
        this.onUpdate.accept(value);
        return this;
    }

    public DataBinding onViewChange(Consumer<T> onViewChange){
        this.onViewChange = onViewChange;
        return this;
    }

    public T getValue() {
        return value;
    }

    public void setValueFromView(T t){
        this.value = t;
        onViewChange.accept(t);
    }

    public DataBinding<T> setValueFromData(T t){
        this.value = t;
        onUpdate.accept(t);
        return this;
    }

    public DataBinding setValueAccessorFromData(Supplier<T> supplier){
        this.valueGetter = supplier;
        if(this.value == null)
            this.value = valueGetter.get();
        return this;
    }

    public void requestNewValue(){
        this.value = valueGetter.get();
        onUpdate.accept(value);
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
