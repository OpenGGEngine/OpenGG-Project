/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.console.GGConsole;
import com.opengg.core.util.ClassUtil;
import com.opengg.core.util.JarClassUtil;
import com.opengg.core.world.components.*;
import com.opengg.core.world.components.particle.*;
import com.opengg.core.world.components.physics.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Javier
 */
public class ViewModelComponentRegistry {
    static List<ViewModelComponentRegisterInfoContainer> registered = new ArrayList<>();
    static List<Class> components = new ArrayList<>();
    static List<Class> viewmodels = new ArrayList<>();
    
    public static void initialize(){
        initializeDefault();
        //registerAllFromJar(Resource.getAbsoluteFromLocal("lib" + File.separator + "com.opengg.core.jar"));
    }
    
    private static void initializeDefault(){
        register(ModelRenderComponent.class);
        register(WaterComponent.class);
        register(WorldObject.class);
        register(PhysicsComponent.class);
        register(TerrainComponent.class);
        register(SunComponent.class);
        register(LightComponent.class);
        register(CameraComponent.class);
        register(ExplosionParticleEmitter.class);
        register(Zone.class);
        
        register(ModelRenderComponentViewModel.class);
        register(WaterComponentViewModel.class);
        register(WorldObjectViewModel.class);
        register(PhysicsComponentViewModel.class);
        register(TerrainComponentViewModel.class);
        register(SunComponentViewModel.class);
        register(LightComponentViewModel.class);
        register(CameraComponentViewModel.class);
        register(ExplosionParticleEmitterViewModel.class);
        register(ZoneViewModel.class);
        
    }
    
    public static void register(Class component){
        register(component, true);
    }
    
    public static void register(List<Class> components){
        for(Class clazz : components){
            register(clazz);
        }
    }
    
    public static void register(Class component, boolean error){
        boolean iscomponent = ClassUtil.childOf(component, Component.class);
        boolean isviewmodel = ClassUtil.childOf(component, ViewModel.class);     
        
        if(iscomponent){
            if(!components.contains(component))
                components.add(component);
        }else if(isviewmodel){
            if(!viewmodels.contains(component))
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
        var vmmap = viewmodels.stream()
                .collect(Collectors.toMap(v -> ((ForComponent)v.getAnnotation(ForComponent.class)).value(), v -> v));

        var regmap = registered.stream()
                .collect(Collectors.toMap(v -> v.component, v -> v));

        var containers = components.stream()
                .filter(c -> vmmap.containsKey(c))
                .map(c -> new ViewModelComponentRegisterInfoContainer(c, vmmap.get(c)))
                .filter(vc -> !regmap.containsKey(vc.component))
                .peek(vc -> regmap.put(vc.component, vc))
                .collect(Collectors.toList());

        registered.addAll(containers);
        
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

    private ViewModelComponentRegistry() {
    }

    /**
     *
     * @author Javier
     */
    public static class ViewModelComponentRegisterInfoContainer {
        private Class component;
        private Class viewmodel;

        public ViewModelComponentRegisterInfoContainer(Class c, Class v){
            this.component = c;
            this.viewmodel = v;
        }

        public Class getComponent(){
            return component;
        }

        public Class getViewmodel(){
            return viewmodel;
        }
    }
}
