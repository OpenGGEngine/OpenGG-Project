/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.SunComponent;

/**
 *
 * @author Javier
 */
@ForComponent(SunComponent.class)
public class SunComponentViewModel extends ViewModel{

    @Override
    public void createMainViewModel() {
        
        Element tex = new Element();
        tex.autoupdate = false;
        tex.type = Element.Type.TEXTURE;
        tex.name = "Sun Texture";
        tex.value = TextureManager.getDefault();
        tex.internalname = "suntex";
        
        Element speed = new Element();
        speed.type = Element.Type.FLOAT;
        speed.name = "Rotation Speed (rad/sec)";
        speed.value = 0.01f;
        speed.internalname = "speed";
        
        Element currot = new Element();
        currot.type = Element.Type.FLOAT;
        currot.name = "Current rotation";
        currot.value = 0f;
        currot.internalname = "currot";
        
        elements.add(tex);
        elements.add(speed);
        elements.add(currot);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        Element tex = new Element();
        tex.autoupdate = false;
        tex.type = Element.Type.TEXTURE;
        tex.name = "Sun Texture";
        tex.value = TextureManager.getDefault();
        tex.internalname = "suntex";
        
        init.addElement(tex);
        return init;
    }

    @Override
    public Component getFromInitializer(Initializer init) {
        return new SunComponent((TextureData)init.elements.get(0).value);
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("speed"))
            ((SunComponent)component).setRotationSpeed((Float)element.value);
        
        if(element.internalname.equals("currot"))
            ((SunComponent)component).setCurrentRotation((Float)element.value);
        
        if(element.internalname.equals("tex"))
            ((SunComponent)component).setTexture(Texture.get2DTexture((TextureData)element.value));
        
    }

    @Override
    public void updateView(Element element) {
        getByName("speed").value = ((SunComponent)component).getRotationSpeed();
        getByName("currot").value = ((SunComponent)component).getCurrentRotation();
    }
}
