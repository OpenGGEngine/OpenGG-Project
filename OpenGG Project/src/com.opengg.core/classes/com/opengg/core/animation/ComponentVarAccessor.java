package com.opengg.core.animation;

import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.Component;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ComponentVarAccessor {
    public static HashMap<Class, HashMap<String, AccessorStruct>> registered = new HashMap<>();

    private static class AccessorStruct {
        public AccessorStruct(Class t, BiConsumer c, Function l) {
            this.t = t;
            this.c = c;
            this.l = l;
        }

        public Class t;
        public BiConsumer c;
        public Function l;
    }

    public static void register(Class c1, Class t, String s, BiConsumer c, Function l) {
        if (!registered.containsKey(c1)) {
            registered.put(c1, new HashMap<>());
        }
        registered.get(c1).put(s, new AccessorStruct(t, c, l));
    }

    public static Object getVar(String s, Object instance) {
        Class c = instance.getClass();
        do {
            System.out.println(c);
            if (c == Object.class) {
                GGConsole.error("No getter defined " + s);
                return null;
            }
            if (registered.containsKey(c)) {
                AccessorStruct b = registered.get(c).get(s);
                if (b != null) {
                    return b.l.apply(instance);
                }
            }
            c = c.getSuperclass();
        } while (true);

    }

    public static void setVar(String s, Object instance, Object value) {
        //Adds Common Lookups for speed purposes.
        Class c = instance.getClass();
        if (instance instanceof Component && processSetComponent(s, instance, value)) return;
        System.out.println("sdsd");
        do {
            if (c == Object.class) {
                GGConsole.error("No setter defined " + s);
                break;
            }
            if (registered.containsKey(c)) {
                AccessorStruct b = registered.get(c).get(s);
                if (b != null) {
                    b.c.accept(instance, value);
                    break;
                }
            }
            c = c.getSuperclass();
        } while (true);

    }

    private static boolean processSetComponent(String s, Object instance, Object value) {
        switch (s) {
            case "position":
                ((Component) instance).setPositionOffset((Vector3f) value);
                break;
            case "rotation":
                ((Component) instance).setRotationOffset((Quaternionf) value);
                break;
            case "scale":
                ((Component) instance).setScaleOffset((Vector3f) value);
                break;
                default:
                    return false;
        }
        return true;
    }
}
