package com.opengg.core.animation;

import com.opengg.core.console.GGConsole;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.Component;

import java.util.HashMap;
import java.util.function.*;

public class ComponentVarAccessor {
    public static HashMap<Class, HashMap<String, AccessorStruct>> registered = new HashMap<>();

    private static class AccessorStruct {
        /**
         * Constructor for INSTANCE METHODS. If you are trying to use static getters and setters
         * use the Consumer and Supplier Constructor
         *
         * @param t
         * @param c
         * @param l
         */
        public AccessorStruct(Class t, BiConsumer c, Function l) {
            this.t = t;
            this.c = c;
            this.l = l;
        }

        /**
         * Constructor for STATIC METHODS. If you are trying to use an instance getter
         * or setter use the BiConsumer and Function constructor.
         *
         * @param t
         * @param c1 Static Setter Method
         * @param l1 Static Getter Method
         */
        public AccessorStruct(Class t, Consumer c1, Supplier l1){
            this.t = t;
            this.c1 = c1;
            this.l2 = l2;
            this.isStatic = true;
        }

        public Class t;
        public BiConsumer c;
        public Function l;
        public Consumer c1;
        public Supplier l2;
        public boolean isStatic;
    }

    public static void register(Class c1, Class t, String s, BiConsumer c, Function l) {
        if (!registered.containsKey(c1)) {
            registered.put(c1, new HashMap<>());
        }
        registered.get(c1).put(s, new AccessorStruct(t, c, l));
    }

    /**
     *
     * @param s Name of the field
     * @param instance Takes in the object for instance getters or null for static getters
     * @return
     */
    public static Object getVar(String s, Object instance) {
        Class c = instance.getClass();
        do {
            if (c == Object.class) {
                GGConsole.error("No getter defined " + s);
                return null;
            }
            if (registered.containsKey(c)) {
                AccessorStruct b = registered.get(c).get(s);
                if (b != null) {
                    if(b.isStatic) {
                        return b.l2.get();
                    }else{
                        return b.l.apply(instance);
                    }
                }
            }
            c = c.getSuperclass();
        } while (true);

    }

    /**
     *
     * @param s
     * @param instance Object to set for instance setters and null for static setters
     * @param value
     */
    public static void setVar(String s, Object instance, Object value) {
        //Adds Common Lookups for speed purposes.
        Class c = instance.getClass();
        if (instance instanceof Component && processSetComponent(s, (Component) instance, value)) return;
        do {
            if (c == Object.class) {
                GGConsole.error("No setter defined " + s);
                break;
            }
            if (registered.containsKey(c)) {
                AccessorStruct b = registered.get(c).get(s);
                if (b != null) {
                    if(b.isStatic) {
                        b.c1.accept(value);
                    }else{
                        b.c.accept(instance, value);
                    }
                    break;
                }
            }
            c = c.getSuperclass();
        } while (true);

    }

    private static boolean processSetComponent(String s, Component instance, Object value) {
        switch (s) {
            case "position":
                instance.setPositionOffset((Vector3f) value);
                break;
            case "rotation":
                instance.setRotationOffset((Quaternionf) value);
                break;
            case "scale":
                instance.setScaleOffset((Vector3f) value);
                break;
                default:
                    return false;
        }
        return true;
    }
}
