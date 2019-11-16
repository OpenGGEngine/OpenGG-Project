/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.world.components.SunComponent;

/**
 *
 * @author Javier
 */
@ForComponent(SunComponent.class)
public class SunComponentViewModel extends ComponentViewModel<SunComponent> {

    @Override
    public void createMainViewModel() {
        
        DataBinding<TextureData> tex = DataBinding.ofType(DataBinding.Type.TEXTURE);
        tex.name = "Sun Texture";
        tex.internalname = "suntex";
        tex.setValueAccessorFromData(() -> model.getTexture().getData().get(0));
        tex.onViewChange(t -> model.setTexture(Texture.get2DTexture(t)));
        
        DataBinding<Float> speed = DataBinding.ofType(DataBinding.Type.FLOAT);
        speed.name = "Rotation Speed (rad/sec)";
        speed.internalname = "speed";
        
        DataBinding<Float> currot = DataBinding.ofType(DataBinding.Type.FLOAT);
        currot.name = "Current rotation";
        currot.internalname = "currot";
        currot.setValueAccessorFromData(model::getCurrentRotation);
        currot.onViewChange(model::setCurrentRotation);

        addElement(tex);
        addElement(speed);
        addElement(currot);
    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {
        var tex = new DataBinding.TextureBinding();
        tex.autoupdate = false;
        tex.name = "Sun Texture";
        tex.setValueFromData(TextureManager.getDefault());
        tex.internalname = "suntex";
        init.addElement(tex);

        return init;
    }

    @Override
    public SunComponent getFromInitializer(BindingAggregate init) {
        return new SunComponent((TextureData)init.getDataBindings().get(0).getValue());
    }
}
