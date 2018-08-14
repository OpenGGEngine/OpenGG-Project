package com.opengg.core.model.ggmodel;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.world.components.RenderComponent;

public class GGRenderComponent extends RenderComponent {

    GGModel model;

    public GGRenderComponent(GGModel model){
        this.model = model;
        this.setFormat(RenderEngine.tangentVAOFormat);
        this.setDrawable(model.getDrawable());
    }

}
