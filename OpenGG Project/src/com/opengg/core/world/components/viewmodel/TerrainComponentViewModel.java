/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.world.Terrain;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.TerrainComponent;

/**
 *
 * @author Javier
 */
public class TerrainComponentViewModel extends ViewModel{

    @Override
    public void createMainViewModel() {       
        Element blot = new Element();
        blot.type = Element.TEXTURE;
        blot.name = "Terrain Blotmap";
        blot.value = TextureManager.getDefault();
        blot.internalname = "blot";
        
        Element t1 = new Element();
        t1.type = Element.TEXTURE;
        t1.name = "Texture 1";
        t1.value = TextureManager.getDefault();
        t1.internalname = "t1";
        
        Element t2 = new Element();
        t2.type = Element.TEXTURE;
        t2.name = "Texture 2";
        t2.value = TextureManager.getDefault();
        t2.internalname = "t2";
        
        Element t3 = new Element();
        t3.type = Element.TEXTURE;
        t3.name = "Texture 3";
        t3.value = TextureManager.getDefault();
        t3.internalname = "t3";
        
        Element t4 = new Element();
        t4.type = Element.TEXTURE;
        t4.name = "Texture 4";
        t4.value = TextureManager.getDefault();
        t4.internalname = "t4";
        
        elements.add(blot);
        elements.add(t1);
        elements.add(t2);
        elements.add(t3);
        elements.add(t4);
    }

    @Override
    public Initializer getInitializer() {
        Initializer init = new Initializer();
        
        Element height = new Element();
        height.type = Element.TEXTURE;
        height.name = "Terrain Heightmap";
        height.value = TextureManager.getDefault();
        height.internalname = "height";
        
        Element blot = new Element();
        blot.type = Element.TEXTURE;
        blot.name = "Terrain Blotmap";
        blot.value = TextureManager.getDefault();
        blot.internalname = "blot";
        
        Element t1 = new Element();
        t1.type = Element.TEXTURE;
        t1.name = "Texture 1";
        t1.value = TextureManager.getDefault();
        t1.internalname = "t1";
        
        Element t2 = new Element();
        t2.type = Element.TEXTURE;
        t2.name = "Texture 2";
        t2.value = TextureManager.getDefault();
        t2.internalname = "t2";
        
        Element t3 = new Element();
        t3.type = Element.TEXTURE;
        t3.name = "Texture 3";
        t3.value = TextureManager.getDefault();
        t3.internalname = "t3";
        
        Element t4 = new Element();
        t4.type = Element.TEXTURE;
        t4.name = "Texture 4";
        t4.value = TextureManager.getDefault();
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
    public Component getFromInitializer(Initializer init) {
        Terrain terrain = Terrain.generate((TextureData)init.elements.get(0).value);
        
        Texture blot = Texture.get2DTexture((TextureData)init.elements.get(1).value);
        Texture tex = Texture.getArrayTexture((TextureData)init.elements.get(2).value, (TextureData)init.elements.get(3).value, (TextureData)init.elements.get(4).value, (TextureData)init.elements.get(5).value);
        
        TerrainComponent comp = new TerrainComponent(terrain);
        comp.setBlotmap(blot);
        comp.setGroundArray(tex);
        
        return comp;
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("blot"))
            ((TerrainComponent)component).setBlotmap(Texture.get2DTexture((TextureData)element.value));
        
        else if(element.internalname.startsWith("t"))
            ((TerrainComponent)component).setGroundArray(Texture.getArrayTexture((TextureData)getByName("t1").value, (TextureData)getByName("t2").value, (TextureData)getByName("t3").value, (TextureData)getByName("t4").value));
    }

    @Override
    public void updateViews() {
        getByName("blot").value = ((TerrainComponent)component).blotmap.getData().get(0);
        getByName("t1").value = ((TerrainComponent)component).array.getData().get(0);
        getByName("t2").value = ((TerrainComponent)component).array.getData().get(1);
        getByName("t3").value = ((TerrainComponent)component).array.getData().get(2);
        getByName("t4").value = ((TerrainComponent)component).array.getData().get(3);
        
    }
}
