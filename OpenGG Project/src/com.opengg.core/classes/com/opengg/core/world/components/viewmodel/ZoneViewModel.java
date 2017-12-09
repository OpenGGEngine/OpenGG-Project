/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.engine.WorldEngine;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.Zone;
import com.opengg.core.world.components.triggers.Triggerable;

/**
 *
 * @author Javier
 */
public class ZoneViewModel extends ViewModel{

    @Override
    public void createMainViewModel() {
        Element lwh = new Element();
        lwh.type = Element.VECTOR3F;
        lwh.autoupdate = true;
        lwh.name = "LWH";
        lwh.internalname = "lwh";
        lwh.visible = true;
        lwh.value = new Vector3f(1,1,1);
        
        Element children = new Element();
        children.type = Element.STRING;
        children.autoupdate = false;
        children.name = "Children";
        children.internalname = "children";
        children.visible = true;
        children.value = ";";
        
        elements.add(lwh);
        elements.add(children);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        Element lwh = new Element();
        lwh.type = Element.VECTOR3F;
        lwh.autoupdate = true;
        lwh.internalname = "lwh";
        lwh.visible = true;
        lwh.value = new Vector3f(1,1,1);
        init.addElement(lwh);
        return init;
    }

    @Override
    public Component getFromInitializer(Initializer init) {
        Zone zone = new Zone(new AABB((Vector3f) init.get("lwh").value));
        return zone;
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equalsIgnoreCase("lwh")){
            ((Zone)component).getBox().setLWH((Vector3f) element.value);
            ((Zone)component).getBox().recalculate();
        }
        if(element.internalname.equalsIgnoreCase("children")){
            String lists = (String) element.value;
            String[] strings = lists.split(";");
            for(String s : strings){
                s = s.trim();
                Component c = WorldEngine.getCurrent().find(s);
                if(c != null && c instanceof Triggerable)
                    ((Zone)component).addSubscriber((Triggerable) c);
            }
        }
    }

    @Override
    public void updateViews() {
        
    }
    
}
