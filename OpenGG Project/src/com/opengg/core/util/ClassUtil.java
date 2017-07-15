/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

/**
 *
 * @author Javier
 */
public class ClassUtil {
    public static boolean childOf(Class child, Class parent){
        boolean valid = false;
        Class clazz = child;
        while(true){
            clazz = clazz.getSuperclass();
            if(clazz == null)
                break;
            
            if(clazz.getCanonicalName().equalsIgnoreCase(parent.getCanonicalName())){
                valid = true;
                break;
            }
        }
        return valid;
    }
}
