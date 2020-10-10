/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

import com.opengg.core.exceptions.ClassInstantiationException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ClassUtil {
    public static List<ClassLoader> loaders = new ArrayList<>();

    public static Object createByName(String name) throws ClassInstantiationException {
        Class clazz = null;
        try{
            clazz = Class.forName(name);
        }catch (ClassNotFoundException ex) {
            try{
                clazz = Class.forName(name, true, JarClassUtil.getLoader());
            }catch(ClassNotFoundException e){
                throw new RuntimeException("Failed to create class " + name + "!");
            }
        }

        try {
            Object nclazz = clazz.getConstructor().newInstance();
            return nclazz;
        } catch (Exception e) {
            throw new ClassInstantiationException(e);
        }
    }

    private ClassUtil() {
    }
}
