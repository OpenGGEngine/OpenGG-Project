/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.Component;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
@ForComponent(Component.class)
public abstract class ViewModel<T extends Component> {
    public T component;
    List<Element> elements = new ArrayList<>();
    
    public ViewModel(){
        Element pos = new Element();
        pos.autoupdate = true;
        pos.type = Element.Type.VECTOR3F;
        pos.name = "Position";
        pos.internalname = "pos";
        pos.value = new Vector3f(0,0,0);
        
        Element rot = new Element();
        rot.autoupdate = true;
        rot.type = Element.Type.VECTOR3F;
        rot.name = "Rotation";
        rot.internalname = "rot";
        rot.value = new Vector3f(0,0,0);
        
        Element scale = new Element();
        scale.autoupdate = true;
        scale.type = Element.Type.VECTOR3F;
        scale.name = "Scale";
        scale.internalname = "scale";
        scale.value = new Vector3f(1,1,1);
        
        Element name = new Element();
        name.autoupdate = true;
        name.type = Element.Type.STRING;
        name.name = "Name";
        name.internalname = "name";
        name.value = "default";
        
        Element update = new Element();
        update.autoupdate = true;
        update.type = Element.Type.BOOLEAN;
        update.name = "Enabled";
        update.internalname = "enabled";
        update.value = true;
        update.forceupdate = true;
        
        Element serialize = new Element();
        serialize.autoupdate = true;
        serialize.type = Element.Type.BOOLEAN;
        serialize.name = "Should serialize";
        serialize.internalname = "serialize";
        serialize.value = true;
        serialize.forceupdate = true;
        
        Element abs = new Element();
        abs.autoupdate = true;
        abs.type = Element.Type.BOOLEAN;
        abs.name = "Is position absolute";
        abs.internalname = "abs";
        abs.value = false;
        abs.forceupdate = true;
        
        elements.add(pos);
        elements.add(rot);
        elements.add(scale);
        elements.add(name);
        elements.add(update);
        elements.add(serialize);
        elements.add(abs);
        
        createMainViewModel();
    }
    
    public abstract void createMainViewModel();
    
    public abstract Initializer getInitializer(Initializer init);
    
    public abstract T getFromInitializer(Initializer init);
    
    public void setComponent(T c){
        this.component = c;
    }
    
    public T getComponent(){
        return component;
    }
    
    public List<Element> getElements(){
        return elements;
    }

    public void addElement(Element element){
        getElements().add(element);
    }
    
    public final void fireEvent(Element element){
        onChangeLocal(element);
    }
    
    final void onChangeLocal(Element element){
        if(component != null){
            switch (element.internalname) {
                case "pos" -> component.setPositionOffset((Vector3f) element.value);
                case "rot" -> component.setRotationOffset(new Quaternionf((Vector3f) element.value));
                case "scale" -> component.setScaleOffset((Vector3f) element.value);
                case "name" -> component.setName((String) element.value);
                case "enabled" -> component.setEnabled((Boolean) element.value);
                case "serialize" -> component.setSerializable((Boolean) element.value);
                case "abs" -> component.setAbsoluteOffset((Boolean) element.value);
            }
            onChange(element); 
        }
    }
    
    public abstract void onChange(Element element);

    public final void updateAll(){
        for(Element element : elements)
            updateLocal(element);
    }

    public final void updateLocal(Element element){
        switch (element.internalname) {
            case "pos" -> element.value = component.getPositionOffset();
            case "rot" -> element.value = component.getRotationOffset().toEuler();
            case "scale" -> element.value = component.getScale();
            case "enabled" -> element.value = component.isEnabled();
            case "serialize" -> element.value = component.shouldSerialize();
            case "abs" -> element.value = component.isAbsoluteOffset();
            case "name" -> element.value = component.getName();
        }
        updateView(element);
    }
    
    public abstract void updateView(Element element);
    
    public final Element getByName(String name){
        for(Element e : elements){
            if(e.internalname.equalsIgnoreCase(name))
                return e;
        }
        return null;
    }
}
