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
public abstract class ComponentViewModel {
    Component component;
    List<ViewModelElement> elements = new ArrayList<>();
    
    public ComponentViewModel(){
        ViewModelElement pos = new ViewModelElement();
        pos.autoupdate = true;
        pos.type = ViewModelElement.VECTOR3F;
        pos.name = "Position";
        pos.internalname = "pos";
        pos.value = new Vector3f(0,0,0);
        pos.cmv = this;
        
        ViewModelElement rot = new ViewModelElement();
        rot.autoupdate = true;
        rot.type = ViewModelElement.VECTOR3F;
        rot.name = "Rotation";
        rot.internalname = "rot";
        rot.value = new Vector3f(0,0,0);
        rot.cmv = this;
        
        ViewModelElement scale = new ViewModelElement();
        scale.autoupdate = true;
        scale.type = ViewModelElement.VECTOR3F;
        scale.name = "Scale";
        scale.internalname = "scale";
        scale.value = new Vector3f(1,1,1);
        scale.cmv = this;
        
        ViewModelElement name = new ViewModelElement();
        name.autoupdate = false;
        name.type = ViewModelElement.STRING;
        name.name = "Name";
        name.internalname = "name";
        name.value = "default";
        name.cmv = this;
        
        ViewModelElement update = new ViewModelElement();
        update.autoupdate = true;
        update.type = ViewModelElement.BOOLEAN;
        update.name = "Enabled";
        update.internalname = "enabled";
        update.value = true;
        update.cmv = this;
        
        ViewModelElement abs = new ViewModelElement();
        abs.autoupdate = true;
        abs.type = ViewModelElement.BOOLEAN;
        abs.name = "Should use absolute position";
        abs.internalname = "abs";
        abs.value = false;
        abs.cmv = this;
        
        elements.add(pos);
        elements.add(rot);
        elements.add(scale);
        elements.add(name);
        elements.add(update);
        elements.add(abs);
        
        createMainViewModel();
    }
    
    public abstract void createMainViewModel();
    
    public abstract ViewModelInitializer getInitializer();
    
    public abstract Component getFromInitializer(ViewModelInitializer init);
    
    public void setComponent(Component c){
        this.component = c;
    }
    
    public Component getComponent(){
        return component;
    }
    
    public List<ViewModelElement> getElements(){
        return elements;
    }
    
    public final void fireEvent(ViewModelElement element){
        onChangeLocal(element);
    }
    
    final void onChangeLocal(ViewModelElement element){
        if(component != null){
            switch (element.internalname) {
                case "pos":
                    component.setPositionOffset((Vector3f)element.value);
                    break;
                case "rot":
                    component.setRotationOffset(new Quaternionf((Vector3f)element.value));
                    break;
                case "scale":
                    component.setScale((Vector3f)element.value);
                    break;
                case "name":
                    component.setName((String)element.value);
                    break;
                case "enabled":
                    component.setEnabled((Boolean)element.value);
                    break;
                case "abs":
                    component.setAbsoluteOffset((Boolean)element.value);
                    break;
            }
            
            onChange(element); 
        }
    }
    
    public abstract void onChange(ViewModelElement element);
    
    public final void updateLocal(){
        for(ViewModelElement element : elements){
            switch (element.internalname) {
                case "pos":
                    element.value = component.getPositionOffset();
                    break;
                case "rot":
                    element.value = component.getRotationOffset().toEuler();
                    break;
                case "scale":
                    element.value = component.getScale();
                    break;
                case "name":
                    element.value = component.getName();
                    break;
                case "enabled":
                    element.value = component.isEnabled();
                    break;
                case "abs":
                    element.value = component.isAbsoluteOffset();
                    break;
            }
            updateViews();
        }
    }
    
    
    public abstract void updateViews();
}
