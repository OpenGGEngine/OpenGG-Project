package com.opengg.core.world;

import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Animator {
    //HashMap<Class, AnimationAccessorSet> components = new HashMap<>();

    public void animateVector3f(Supplier<Vector3f> supplier, Consumer<Vector3f> consumer, String animtype){

    }

    public void animateVector3f(Component component, String value, String animtype){
        AnimationAccessorSet set;
        component.getAnimationAccessors(set = new AnimationAccessorSet());

        var supplier = set.getVector3fMap().get(value).x;
        var collector = set.getVector3fMap().get(value).y;

        animateVector3f(supplier, collector, animtype);
    }
}
