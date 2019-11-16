/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.TextureManager;
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
        tex.setValueAccessorFromData(model::getTexture);
        tex.onViewChange(model::setTexture);
        
        var texsize = new DataBinding.FloatBinding();
        texsize.internalname = "size";
        texsize.name = "Water Texture Scale";
        texsize.setValueAccessorFromData(model::getTextureScale);
        texsize.onViewChange(model::setTextureScale);
        
        var movementspeed = new DataBinding.FloatBinding();
        movementspeed.internalname = "speed";
        movementspeed.name = "Texture Animation Speed";
        movementspeed.setValueAccessorFromData(model::getMovespeed);
        movementspeed.onViewChange(model::setMovespeed);

        addElement(tex);
        addElement(texsize);
        addElement(movementspeed);
    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {

        var tex = new DataBinding.TextureBinding();
        tex.internalname = "tex";
        tex.name = "Water Texture";
        tex.autoupdate = true;
        tex.setValueFromData(TextureManager.getDefault());
        init.addElement(tex);

        return init;
    }

    @Override
    public WaterComponent getFromInitializer(BindingAggregate init) {
        return new WaterComponent(((TextureData)init.getDataBindings().get(0).getValue()), 1);
    }
}
