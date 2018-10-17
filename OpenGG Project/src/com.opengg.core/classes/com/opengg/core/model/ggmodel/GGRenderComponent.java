package com.opengg.core.model.ggmodel;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.world.components.RenderComponent;

import static org.lwjgl.opengl.GL11.glGetError;

public class GGRenderComponent extends RenderComponent {

    GGModel model;

    public GGRenderComponent(GGModel model){
        this.model = model;
        this.setFormat(model.isAnim?RenderEngine.tangentAnimVAOFormat:RenderEngine.tangentVAOFormat);
        this.setDrawable(model.getDrawable());
    }
    @Override
    public void render(){
        super.render();
    }

}
