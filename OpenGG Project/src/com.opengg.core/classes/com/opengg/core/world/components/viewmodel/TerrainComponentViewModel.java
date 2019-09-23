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
import com.opengg.core.world.Terrain;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.TerrainComponent;

import java.util.List;

/**
 *
 * @author Javier
 */
@ForComponent(TerrainComponent.class)
public class TerrainComponentViewModel extends ViewModel<TerrainComponent>{

    @Override
    public void createMainViewModel() {
        var blot = new DataBinding.TextureBinding();
        blot.name = "Blotmap";
        blot.internalname = "blot";
        blot.setValueAccessorFromData(() -> component.getBlotmap().getData().get(0));
        blot.onViewChange(t -> component.setBlotmap(t));
        
        var t1 = new DataBinding.TextureBinding();
        t1.name = "Texture 1";
        t1.internalname = "t1";
        t1.setValueAccessorFromData(() -> component.getGroundArray().getData().get(0));
        t1.onViewChange(t -> component.setIndividualGroundArrayValue(t,0));

        var t2 = new DataBinding.TextureBinding();
        t2.name = "Texture 2";
        t2.internalname = "t2";
        t2.setValueAccessorFromData(() -> component.getGroundArray().getData().get(1));
        t2.onViewChange(t -> component.setIndividualGroundArrayValue(t,1));

        var t3 = new DataBinding.TextureBinding();
        t3.name = "Texture 3";
        t3.internalname = "t3";
        t3.setValueAccessorFromData(() -> component.getGroundArray().getData().get(2));
        t3.onViewChange(t -> component.setIndividualGroundArrayValue(t,2));

        var t4 = new DataBinding.TextureBinding();
        t4.name = "Texture 4";
        t4.internalname = "t4";
        t4.setValueAccessorFromData(() -> component.getGroundArray().getData().get(3));
        t4.onViewChange(t -> component.setIndividualGroundArrayValue(t,3));
        
        addElement(blot);
        addElement(t1);
        addElement(t2);
        addElement(t3);
        addElement(t4);
        
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        var height = new DataBinding.TextureBinding();
        height.name = "Terrain Heightmap";
        height.setValueFromData(TextureManager.getDefault());
        height.internalname = "height";
        
        var blot = new DataBinding.TextureBinding();
        blot.name = "Terrain Blotmap";
        blot.setValueFromData(TextureManager.getDefault());
        blot.internalname = "blot";
        
        var t1 = new DataBinding.TextureBinding();
        t1.name = "Texture 1";
        t1.setValueFromData(TextureManager.getDefault());
        t1.internalname = "t1";
        
        var t2 = new DataBinding.TextureBinding();
        t2.name = "Texture 2";
        t2.setValueFromData(TextureManager.getDefault());
        t2.internalname = "t2";
        
        var t3 = new DataBinding.TextureBinding();
        t3.name = "Texture 3";
        t3.setValueFromData(TextureManager.getDefault());
        t3.internalname = "t3";
        
        var t4 = new DataBinding.TextureBinding();
        t4.name = "Texture 4";
        t4.setValueFromData(TextureManager.getDefault());
        t4.internalname = "t4";
        
        init.addElement(height);
        init.addElement(blot);
        init.addElement(t1);
        init.addElement(t2);
        init.addElement(t3);
        init.addElement(t4);
        
        return init;
    }

    @Override
    public TerrainComponent getFromInitializer(Initializer init) {
        Terrain terrain = Terrain.generate((TextureData)init.get("height").getValue());

        TerrainComponent comp = new TerrainComponent(terrain);
        comp.setBlotmap((TextureData)init.get("blot").getValue());
        comp.setGroundArray(List.of(
                (TextureData)init.get("t1").getValue(),
                (TextureData)init.get("t2").getValue(),
                (TextureData)init.get("t3").getValue(),
                (TextureData)init.get("t4").getValue()));
        
        return comp;
    }
}
