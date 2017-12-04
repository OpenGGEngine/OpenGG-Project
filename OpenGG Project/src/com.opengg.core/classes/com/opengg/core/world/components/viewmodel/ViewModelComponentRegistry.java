/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.util.ClassUtil;
import com.opengg.core.util.JarClassUtil;
import com.opengg.core.world.components.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ViewModelComponentRegistry {
    static List<ViewModelComponentRegisterInfoContainer> registered = new ArrayList<>();
    static List<Class> components = new ArrayList<>();
    static List<Class> viewmodels = new ArrayList<>();
    
    public static void initialize(){
        registerAllFromJar(Resource.getAbsoluteFromLocal("lib" + File.separator + "com.opengg.core.jar"));
    }
    
    public static void register(Class component){
        register(component, true);
    }
    
    public static void register(Class component, boolean error){
        boolean iscomponent = ClassUtil.childOf(component, Component.class);
        boolean isviewmodel = ClassUtil.childOf(component, ViewModel.class);     
        
        if(iscomponent){
            components.add(component);
        }else if(isviewmodel){
            viewmodels.add(component);
        }else if(error){
            GGConsole.warn("Class named " + component.getCanonicalName() + " failed to register as a component or viewmodel due to not being a child of either");
        }
    }
    
    public static void register(String classname){
        try {
            Class clazz = Class.forName(classname);
            register(clazz);
        } catch (ClassNotFoundException ex) {
            GGConsole.warn("Failed to register class named " + classname + ", class loader could not find it!");
        }
    }
    
    public static void createRegisters(){
        classsearch: for(Class c : components){
            for(Class vm : viewmodels){
                if(c.getSimpleName().equals(vm.getSimpleName().replace("ViewModel", ""))){
                    ViewModelComponentRegisterInfoContainer register = new ViewModelComponentRegisterInfoContainer();
                    register.component = c;
                    register.viewmodel = vm;
                    registered.add(register);
                    continue classsearch;
                }
            }
        }
        
        GGConsole.log("Created and matched viewmodel registries, found " + (registered.size()) + " component types with viewmodels and " + (components.size() - registered.size()) + " without");
    }
    
    public static void registerAllFromJar(String jarpath){
        try{
            List<Class> classes = JarClassUtil.loadAllClassesFromJar(jarpath);
            for(Class clazz : classes){
                register(clazz, false);
            }
        }catch(Exception e){
            GGConsole.warn("Failed to load jarfile at " + jarpath + ", viewmodels for classes in that jar may be missing!");
        }
    }
    
    public static void clearRegistry(){
        components.clear();
        viewmodels.clear();
        registered.clear();
    }
    
    public static Class findViewModel(Class component){
        for(ViewModelComponentRegisterInfoContainer container : registered){
            if(container.component.equals(component))
                return container.viewmodel;
        }
        return null;
    }
    
    public static List<ViewModelComponentRegisterInfoContainer> getAllRegistries(){
        return registered;
    }
}
