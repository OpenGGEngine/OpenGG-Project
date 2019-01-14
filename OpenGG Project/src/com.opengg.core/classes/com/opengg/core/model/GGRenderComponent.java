package com.opengg.core.model;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.world.components.RenderComponent;

public class GGRenderComponent extends RenderComponent {
    private Model model;

    public GGRenderComponent(Model model){
        this.model = model;
        this.setFormat(model.isAnim? RenderEngine.tangentAnimVAOFormat: RenderEngine.tangentVAOFormat);
        this.setDrawable(model.getDrawable());
    }
    @Override
    public void render(){
        super.render();
    }

}
