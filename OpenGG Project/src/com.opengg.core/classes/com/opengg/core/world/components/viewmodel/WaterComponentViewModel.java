/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.Initializer;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.WaterComponent;

/**
 *
 * @author Javier
 */
@ForComponent(WaterComponent.class)
public class WaterComponentViewModel extends RenderComponentViewModel<WaterComponent>{

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var tex = new DataBinding.TextureBinding();
        tex.internalname = "tex";
        tex.name = "Water Texture";
        tex.autoupdate = true;
        tex.setValueAccessorFromData(component::getTexture);
        tex.onViewChange(component::setTexture);
        
        var texsize = new DataBinding.FloatBinding();
        texsize.internalname = "size";
        texsize.name = "Water Texture Scale";
        texsize.setValueAccessorFromData(component::getTextureScale);
        texsize.onViewChange(component::setTextureScale);
        
        var movementspeed = new DataBinding.FloatBinding();
        movementspeed.internalname = "speed";
        movementspeed.name = "Texture Animation Speed";
        movementspeed.setValueAccessorFromData(component::getMovespeed);
        movementspeed.onViewChange(component::setMovespeed);

        addElement(tex);
        addElement(texsize);
        addElement(movementspeed);
    }

    @Override
    public Initializer getInitializer(Initializer init) {

        var tex = new DataBinding.TextureBinding();
        tex.internalname = "tex";
        tex.name = "Water Texture";
        tex.autoupdate = true;
        tex.setValueFromData(TextureManager.getDefault());
        init.addElement(tex);

        return init;
    }

    @Override
    public WaterComponent getFromInitializer(Initializer init) {
        return new WaterComponent(((TextureData)init.dataBindings.get(0).getValue()), 1);
    }
}
