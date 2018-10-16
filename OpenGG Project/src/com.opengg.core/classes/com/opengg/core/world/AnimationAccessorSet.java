package com.opengg.core.world;

import com.opengg.core.math.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AnimationAccessorSet {
    private Map<String, Tuple<Supplier<Vector2f>, Consumer<Vector2f>>> vector2fMap = new HashMap<>();
    private Map<String, Tuple<Supplier<Vector3f>, Consumer<Vector3f>>> vector3fMap = new HashMap<>();
    private Map<String, Tuple<Supplier<Vector4f>, Consumer<Vector4f>>> vector4fMap = new HashMap<>();
    private Map<String, Tuple<Supplier<Quaternionf>, Consumer<Quaternionf>>> quaternionMap = new HashMap<>();
    private Map<String, Tuple<Supplier<Float>, Consumer<Float>>> floatMap = new HashMap<>();
    private Map<String, Tuple<Supplier<Integer>, Consumer<Integer>>> intMap = new HashMap<>();

    public void addVector2f(String name, Supplier<Vector2f> supplier, Consumer<Vector2f> consumer){
        vector2fMap.put(name, new Tuple<>(supplier, consumer));
    }

    public void addVector3f(String name, Supplier<Vector3f> supplier, Consumer<Vector3f> consumer){
        vector3fMap.put(name, new Tuple<>(supplier, consumer));
    }

    public void addVector4f(String name, Supplier<Vector4f> supplier, Consumer<Vector4f> consumer){
        vector4fMap.put(name, new Tuple<>(supplier, consumer));
    }

    public void addQuaternionf(String name, Supplier<Quaternionf> supplier, Consumer<Quaternionf> consumer){
        quaternionMap.put(name, new Tuple<>(supplier, consumer));
    }

    public void addFloat(String name, Supplier<Float> supplier, Consumer<Float> consumer){
        floatMap.put(name, new Tuple<>(supplier, consumer));
    }

    public void addInteger(String name, Supplier<Integer> supplier, Consumer<Integer> consumer){
        intMap.put(name, new Tuple<>(supplier, consumer));
    }

    public Map<String, Tuple<Supplier<Vector2f>, Consumer<Vector2f>>> getVector2fMap() {
        return Collections.unmodifiableMap(vector2fMap);
    }

    public Map<String, Tuple<Supplier<Vector3f>, Consumer<Vector3f>>> getVector3fMap() {
        return Collections.unmodifiableMap(vector3fMap);
    }

    public Map<String, Tuple<Supplier<Vector4f>, Consumer<Vector4f>>> getVector4fMap() {
        return Collections.unmodifiableMap(vector4fMap);
    }

    public Map<String, Tuple<Supplier<Quaternionf>, Consumer<Quaternionf>>> getQuaternionMap() {
        return Collections.unmodifiableMap(quaternionMap);
    }

    public Map<String, Tuple<Supplier<Float>, Consumer<Float>>> getFloatMap() {
        return Collections.unmodifiableMap(floatMap);
    }

    public Map<String, Tuple<Supplier<Integer>, Consumer<Integer>>> getIntMap() {
        return Collections.unmodifiableMap(intMap);
    }
}
