/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.Initializer;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.world.components.SunComponent;

/**
 *
 * @author Javier
 */
@ForComponent(SunComponent.class)
public class SunComponentViewModel extends ViewModel<SunComponent>{

    @Override
    public void createMainViewModel() {
        
        DataBinding<TextureData> tex = DataBinding.ofType(DataBinding.Type.TEXTURE);
        tex.name = "Sun Texture";
        tex.internalname = "suntex";
        tex.setValueAccessorFromData(() -> component.getTexture().getData().get(0));
        tex.onViewChange(t -> component.setTexture(Texture.get2DTexture(t)));
        
        DataBinding<Float> speed = DataBinding.ofType(DataBinding.Type.FLOAT);
        speed.name = "Rotation Speed (rad/sec)";
        speed.internalname = "speed";
        
        DataBinding<Float> currot = DataBinding.ofType(DataBinding.Type.FLOAT);
        currot.name = "Current rotation";
        currot.internalname = "currot";
        currot.setValueAccessorFromData(component::getCurrentRotation);
        currot.onViewChange(component::setCurrentRotation);

        addElement(tex);
        addElement(speed);
        addElement(currot);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        var tex = new DataBinding.TextureBinding();
        tex.autoupdate = false;
        tex.name = "Sun Texture";
        tex.setValueFromData(TextureManager.getDefault());
        tex.internalname = "suntex";
        init.addElement(tex);

        return init;
    }

    @Override
    public SunComponent getFromInitializer(Initializer init) {
        return new SunComponent((TextureData)init.dataBindings.get(0).getValue());
    }
}
