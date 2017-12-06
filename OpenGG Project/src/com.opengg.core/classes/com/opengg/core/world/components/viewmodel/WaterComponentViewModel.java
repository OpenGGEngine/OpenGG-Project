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
import com.opengg.core.world.components.WaterComponent;

/**
 *
 * @author Javier
 */
public class WaterComponentViewModel extends ViewModel{

    @Override
    public void createMainViewModel() {
        Element tex = new Element();
        tex.type = Element.TEXTURE;
        tex.internalname = "tex";
        tex.name = "Water Texture";
        tex.autoupdate = true;
        tex.value = TextureManager.getDefault();
        
        Element texsize = new Element();
        texsize.type = Element.FLOAT;
        texsize.internalname = "size";
        texsize.name = "Water Texture Scale";
        texsize.value = 0.1f;
        
        Element movementspeed = new Element();
        movementspeed.type = Element.FLOAT;
        movementspeed.internalname = "speed";
        movementspeed.name = "Texture Animation Speed";
        movementspeed.value = 100f;
        
        elements.add(tex);
        elements.add(texsize);
        elements.add(movementspeed);
    }

    @Override
    public Initializer getInitializer(Initializer init) {

        Element tex = new Element();
        tex.type = Element.TEXTURE;
        tex.internalname = "tex";
        tex.name = "Water Texture";
        tex.autoupdate = true;
        tex.value = TextureManager.getDefault();
        init.addElement(tex);
        
        Element size = new Element();
        size.type = Element.FLOAT;
        size.internalname = "size";
        size.name = "Water Mesh Size";
        size.autoupdate = true;
        size.value = 100f;
        init.addElement(size);
        
        return init;
    }

    @Override
    public Component getFromInitializer(Initializer init) {
        WaterComponent wc = new WaterComponent(Texture.get2DTexture((TextureData)init.elements.get(0).value), (Float)init.elements.get(1).value);
        return wc;
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("size"))
            ((WaterComponent)component).setTscale((Float)element.value);
        
        if(element.internalname.equals("speed"))
            ((WaterComponent)component).setMovespeed((Float)element.value);
        
        if(element.internalname.equals("tex"))
            ((WaterComponent)component).setTexture(Texture.get2DTexture((TextureData)element.value));
        
    }

    @Override
    public void updateViews() {
        getByName("size").value = ((WaterComponent)component).getTscale();
        getByName("speed").value = ((WaterComponent)component).getMovespeed();
    }
    
}
